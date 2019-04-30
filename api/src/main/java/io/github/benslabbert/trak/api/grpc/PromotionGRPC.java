package io.github.benslabbert.trak.api.grpc;

import com.google.common.annotations.Beta;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.api.rabbitmq.rpc.AddProductRPC;
import io.github.benslabbert.trak.api.service.TakealotAPIService;
import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.ProductImage;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequestFactory;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionGRPC extends PromotionServiceGrpc.PromotionServiceImplBase {

  private final TakealotAPIService takealotAPIService;
  private final ProductService productService;
  private final AddProductRPC addProductRPC;
  private final SellerService sellerService;
  private final PriceService priceService;

  @Override
  public void promotions(
      PromotionRequest request, StreamObserver<PromotionResponse> responseObserver) {

    PromotionRequest.DealCase dealCase = request.getDealCase();

    if (dealCase.equals(PromotionRequest.DealCase.DAILY_DEAL)) {

      log.info("Daily deal promotion requested");
      getDailyDeals(responseObserver);

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

  private void getDailyDeals(StreamObserver<PromotionResponse> responseObserver) {

    List<Long> plIds = takealotAPIService.getPLIDsOnPromotion(Promotion.DAILY_DEAL);
    List<Product> products = productService.findAllByPLIDsIn(plIds);

    // todo duplicate
    if (products.size() != plIds.size()) {
      log.warn("Not all Daily Deal items are in db, add one by one ...");
      Optional<Seller> seller = sellerService.findByNameEquals("Takealot");

      if (seller.isEmpty()) {
        log.warn("Failed to find Takealot seller!");
        Status status =
            Status.newBuilder()
                .setCode(Code.INTERNAL.getNumber())
                .setMessage("Failed to find Seller")
                .build();

        responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        responseObserver.onCompleted();
        return;
      }

      List<Long> productPLIds =
          products.stream().map(Product::getPlId).collect(Collectors.toList());
      plIds.removeAll(productPLIds);

      for (Long plId : plIds) {
        Optional<Long> productId =
            addProductRPC.addProduct(
                AddProductRPCRequestFactory.create(
                    URI.create(getApiUrl(plId)), seller.get(), plId));

        if (productId.isEmpty()) {
          log.warn("Failed to add product for plId: {}", plId);
          continue;
        }

        Optional<Product> p = productService.findOne(productId.get());

        if (p.isPresent()) {
          products.add(p.get());
        } else {
          log.warn("NO product for id: {}", productId);
        }
      }
    }

    List<ProductMessage> items = getLatestResponseItems(products);

    PromotionResponse latestResponse = PromotionResponse.newBuilder().addAllProducts(items).build();

    responseObserver.onNext(latestResponse);
  }

  private String getApiUrl(long plId) {
    return "https://api.takealot.com/rest/v-1-8-0/product-details/PLID"
        + plId
        + "?platform=desktop";
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
