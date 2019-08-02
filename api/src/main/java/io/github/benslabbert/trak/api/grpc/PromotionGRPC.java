package io.github.benslabbert.trak.api.grpc;

import com.google.common.annotations.Beta;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.ProductImage;
import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.PromotionEntityService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionGRPC extends PromotionServiceGrpc.PromotionServiceImplBase {

  private final PromotionEntityService promotionEntityService;
  private final ProductService productService;
  private final PriceService priceService;

  @Override
  public void promotions(
      PromotionRequest request, StreamObserver<PromotionResponse> responseObserver) {
    log.info("Handling promotions gRPC");

    PromotionRequest.DealCase dealCase = request.getDealCase();

    if (dealCase.equals(PromotionRequest.DealCase.DAILY_DEAL)) {
      log.info("Daily deal promotion requested");
      getDailyDeals(request.getPageRequest(), responseObserver);
    } else if (dealCase.equals(PromotionRequest.DealCase.SALE_DEAL)) {
      log.info("Sale promotion requested");
      getSaleDeals(responseObserver);
    } else if (dealCase.equals(PromotionRequest.DealCase.DEAL_NOT_SET)) {
      log.info("Deal oneof not set");

      Status status =
          Status.newBuilder()
              .setCode(Code.INVALID_ARGUMENT.getNumber())
              .setMessage("Deal oneof must be set")
              .build();

      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
    }

    responseObserver.onCompleted();
  }

  @Beta
  private void getSaleDeals(StreamObserver<PromotionResponse> responseObserver) {
    Status status =
        Status.newBuilder()
            .setCode(Code.UNIMPLEMENTED.getNumber())
            .setMessage("Sale Deals not yet implemented")
            .build();

    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
  }

  private void getDailyDeals(
      PageRequestMessage pageRequest, StreamObserver<PromotionResponse> responseObserver) {

    Optional<PromotionEntity> latest =
        promotionEntityService.findLatest(Promotion.DAILY_DEAL.getName());

    if (latest.isEmpty()) {
      log.warn("No daily deals available");
      Status status =
          Status.newBuilder()
              .setCode(Code.UNAVAILABLE.getNumber())
              .setMessage("Daily deals unavailable")
              .build();

      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      return;
    }

    List<Long> plIds =
        latest.get().getProducts().stream().map(Product::getPlId).collect(Collectors.toList());

    Page<Product> page =
        productService.findAllByPLIDsIn(
            plIds, PageRequest.of(pageRequest.getPage(), pageRequest.getPageLen()));

    List<ProductMessage> items = getLatestResponseItems(page.getContent());

    PromotionResponse latestResponse =
        PromotionResponse.newBuilder()
            .addAllProducts(items)
            .setPageResponse(getPageResponse(page))
            .build();

    responseObserver.onNext(latestResponse);
  }

  // todo duplicate in LatestGRPC#getPageResponse
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

  private List<ProductMessage> getLatestResponseItems(List<Product> products) {
    return products.stream()
        .map(
            f ->
                ProductMessage.newBuilder()
                    .addAllCategories(
                        f.getCategories().stream()
                            .map(
                                c ->
                                    CategoryMessage.newBuilder()
                                        .setId(c.getId())
                                        .setName(c.getName())
                                        .build())
                            .collect(Collectors.toList()))
                    .setId(f.getId())
                    .setName(f.getName())
                    .setPrice(getPrice(f))
                    .setProductUrl(f.getUrl())
                    .setImageUrl(getProductImageUrl(f))
                    .build())
        .collect(Collectors.toList());
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
