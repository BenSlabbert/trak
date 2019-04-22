package io.github.benslabbert.trak.api.grpc;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class CategoryGRPC extends CategoryServiceGrpc.CategoryServiceImplBase {

  private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
  private final CategoryService categoryService;
  private final ProductService productService;

  public CategoryGRPC(
      ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub,
      CategoryService categoryService,
      ProductService productService) {

    this.productServiceBlockingStub = productServiceBlockingStub;
    this.categoryService = categoryService;
    this.productService = productService;
  }

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
        responseObserver.onCompleted();
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

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
