package io.github.benslabbert.trak.api.grpc.service;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.core.grpc.ClientCancelRequest;
import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.BrandService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class BrandGRPC extends BrandServiceGrpc.BrandServiceImplBase {

  private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
  private final ProductService productService;
  private final BrandService brandService;

  public BrandGRPC(
      ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub,
      ProductService productService,
      BrandService brandService) {

    this.productServiceBlockingStub = productServiceBlockingStub;
    this.productService = productService;
    this.brandService = brandService;
  }

  @Override
  public void brand(BrandRequest request, StreamObserver<BrandResponse> responseObserver) {

    long brandId = request.getBrandId();

    log.debug("Searching for brand with id: {}", brandId);

    Optional<Brand> brand = brandService.findById(brandId);

    if (brand.isEmpty()) {
      log.warn("No brand for id: {}", brandId);

      Status status =
          Status.newBuilder()
              .setCode(Code.INVALID_ARGUMENT.getNumber())
              .setMessage("No brand found for id: " + brandId)
              .build();

      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      responseObserver.onCompleted();
      return;
    }

    Page<Product> all = productService.findAll(brand.get(), PageRequest.of(0, 12));

    BrandResponse.Builder builder = BrandResponse.newBuilder();

    for (Product product : all) {
      builder.addProducts(
          productServiceBlockingStub
              .product(ProductRequest.newBuilder().setProductId(product.getId()).build())
              .getProduct());
    }

    if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      responseObserver.onCompleted();
      return;
    }

    builder.setBrandId(brand.get().getId());
    builder.setName(brand.get().getName());

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
