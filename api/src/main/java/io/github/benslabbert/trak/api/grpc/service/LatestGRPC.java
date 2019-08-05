package io.github.benslabbert.trak.api.grpc.service;

import io.github.benslabbert.trak.core.grpc.ClientCancelRequest;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.ProductImage;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LatestGRPC extends LatestServiceGrpc.LatestServiceImplBase {

  private final ProductService productService;
  private final PriceService priceService;

  @Override
  public void latest(Empty request, StreamObserver<LatestResponse> responseObserver) {
    Page<Product> products =
        productService.findAll(PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "id")));

    List<ProductMessage> items = getLatestResponseItems(products);

    PageResponse pageResponse = getPageResponse(products);

    LatestResponse latestResponse =
        LatestResponse.newBuilder().addAllProducts(items).setPage(pageResponse).build();

    if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      responseObserver.onCompleted();
      return;
    }

    responseObserver.onNext(latestResponse);
    responseObserver.onCompleted();
  }

  private PageResponse getPageResponse(Page<Product> products) {
    return PageResponse.newBuilder()
        .setCurrentPageNumber(products.getNumber() + 1L)
        .setIsFirstPage(products.isFirst())
        .setIsLastPage(products.isLast())
        .setLastPageNumber(products.getTotalPages())
        .setTotalItems(products.getTotalElements())
        .setPageSize(products.getNumberOfElements())
        .build();
  }

  private List<ProductMessage> getLatestResponseItems(Page<Product> products) {
    return products.getContent().stream().map(getProduct()).collect(Collectors.toList());
  }

  private Function<Product, ProductMessage> getProduct() {
    return p ->
        ProductMessage.newBuilder()
            .setId(p.getId())
            .setName(p.getName())
            .setPrice(getPrice(p))
            .setProductUrl(p.getUrl())
            .setImageUrl(getProductImageUrl(p))
            .build();
  }

  private String getProductImageUrl(Product p) {
    List<ProductImage> images = p.getImages();

    if (images.isEmpty()) {
      return "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMTg4IiBoZWlnaHQ9IjIyNiIgdmlld0JveD0iMCAwIDE4OCAyMjYiPgogICAgPGRlZnM+CiAgICAgICAgPHBhdGggaWQ9ImEiIGQ9Ik0wIDBoMTMxLjI0MkwxODggNjB2MTY2SDBWMHoiLz4KICAgICAgICA8bWFzayBpZD0iYyIgd2lkdGg9IjE4OCIgaGVpZ2h0PSIyMjYiIHg9IjAiIHk9IjAiIGZpbGw9IiNmZmYiPgogICAgICAgICAgICA8dXNlIHhsaW5rOmhyZWY9IiNhIi8+CiAgICAgICAgPC9tYXNrPgogICAgICAgIDxwYXRoIGlkPSJiIiBkPSJNMTg1LjY1IDEwaC43djY3aC02NXYtNS42MzEiLz4KICAgICAgICA8bWFzayBpZD0iZCIgd2lkdGg9IjY1IiBoZWlnaHQ9IjY3IiB4PSIwIiB5PSIwIiBmaWxsPSIjZmZmIj4KICAgICAgICAgICAgPHVzZSB4bGluazpocmVmPSIjYiIvPgogICAgICAgIDwvbWFzaz4KICAgIDwvZGVmcz4KICAgIDxnIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0iZXZlbm9kZCI+CiAgICAgICAgPHVzZSBzdHJva2U9IiNEQURBREEiIHN0cm9rZS1kYXNoYXJyYXk9IjEwLDEwIiBzdHJva2Utd2lkdGg9IjEwIiBtYXNrPSJ1cmwoI2MpIiB4bGluazpocmVmPSIjYSIvPgogICAgICAgIDx0ZXh0IGZpbGw9IiNEM0QzRDMiIGZvbnQtZmFtaWx5PSJIZWx2ZXRpY2FOZXVlLUJvbGQsIEhlbHZldGljYSBOZXVlIiBmb250LXNpemU9IjMwIiBmb250LXdlaWdodD0iYm9sZCI+CiAgICAgICAgICAgIDx0c3BhbiB4PSIyOSIgeT0iMTE5Ij5ObyA8L3RzcGFuPiA8dHNwYW4geD0iMjkiIHk9IjE1MSI+UHJvZHVjdCA8L3RzcGFuPiA8dHNwYW4geD0iMjkiIHk9IjE4MyI+SW1hZ2U8L3RzcGFuPgogICAgICAgIDwvdGV4dD4KICAgICAgICA8dXNlIHN0cm9rZT0iI0RBREFEQSIgc3Ryb2tlLWRhc2hhcnJheT0iMTAsMTAiIHN0cm9rZS13aWR0aD0iMTAiIG1hc2s9InVybCgjZCkiIHRyYW5zZm9ybT0icm90YXRlKDkwIDE1My44NSA0My41KSIgeGxpbms6aHJlZj0iI2IiLz4KICAgIDwvZz4KPC9zdmc+Cg==";
    }

    return images.get(0).getUrl();
  }

  private String getPrice(Product f) {
    Optional<Price> price = priceService.findLatestByProductId(f.getId());
    return price.map(p -> "R" + p.getCurrentPrice()).orElse("R ???");
  }
}
