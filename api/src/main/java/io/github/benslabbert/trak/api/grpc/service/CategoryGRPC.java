package io.github.benslabbert.trak.api.grpc.service;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.core.grpc.ClientCancelRequest;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryGRPC extends CategoryServiceGrpc.CategoryServiceImplBase {

  private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
  private final CategoryService categoryService;
  private final ProductService productService;

  @Override
  public void category(CategoryRequest request, StreamObserver<BrandResponse> responseObserver) {
    long categoryId = request.getCategoryId();

    log.debug("Searching for brand with id: {}", categoryId);

    Optional<Category> category = categoryService.findById(categoryId);

    if (category.isEmpty()) {
      log.warn("No category for id: {}", categoryId);

      Status status =
          Status.newBuilder()
              .setCode(Code.INVALID_ARGUMENT.getNumber())
              .setMessage("No brand found for id: " + categoryId)
              .build();

      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      return;
    }

    Page<Product> all = productService.findAll(category.get(), PageRequest.of(0, 12));

    BrandResponse.Builder builder = BrandResponse.newBuilder();

    for (Product product : all) {
      builder.addProducts(
          productServiceBlockingStub
              .product(ProductRequest.newBuilder().setProductId(product.getId()).build())
              .getProduct());
    }

    builder.setBrandId(category.get().getId());
    builder.setName(category.get().getName());

    Deadline deadline = Context.current().getDeadline();
    if (deadline != null && deadline.isExpired()) {
      log.warn("Request took too long to process");
      responseObserver.onCompleted();
      return;
    } else if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      return;
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
