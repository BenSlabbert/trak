package io.github.benslabbert.trak.api.grpc;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.ProductImage;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.grpc.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProductGRPC extends ProductServiceGrpc.ProductServiceImplBase {

  private final ProductService productService;
  private final PriceService priceService;

  public ProductGRPC(ProductService productService, PriceService priceService) {
    this.productService = productService;
    this.priceService = priceService;
  }

  @Override
  public void product(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {

    Optional<Product> product = productService.findOne(request.getProductId());

    log.debug("Product request");

    if (product.isPresent()) {

      ProductStatsResponse statsResponse = getProductStatsResponse(request);
      ProductResponse productResponse = getProductResponse(product.get(), statsResponse);

      responseObserver.onNext(productResponse);
      responseObserver.onCompleted();

    } else {
      Status status =
          Status.newBuilder()
              .setCode(Code.INVALID_ARGUMENT.getNumber())
              .setMessage("No product found for id: " + request.getProductId())
              .build();

      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
    }
  }

  private ProductResponse getProductResponse(Product product, ProductStatsResponse statsResponse) {

    return ProductResponse.newBuilder()
        .setProduct(
            ProductMessage.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(getPrice(product))
                .setProductUrl(product.getUrl())
                .setImageUrl(getProductImageUrl(product))
                .setBrand(
                    BrandMessage.newBuilder()
                        .setName(product.getBrand().getName())
                        .setId(product.getBrand().getId())
                        .build())
                .addAllCategories(
                    product.getCategories().stream()
                        .map(
                            c ->
                                CategoryMessage.newBuilder()
                                    .setId(c.getId())
                                    .setName(c.getName())
                                    .build())
                        .collect(Collectors.toList()))
                .build())
        .setStats(statsResponse)
        .build();
  }

  private ProductStatsResponse getProductStatsResponse(ProductRequest request) {

    Page<Price> prices =
        priceService.findAllByProductId(request.getProductId(), PageRequest.of(0, 10));

    ProductStatsResponse.Builder statsBuilder = ProductStatsResponse.newBuilder();

    long min = 0L;
    long max = 0L;
    double total = 0L;
    int count = 0;

    for (Price price : prices) {

      count++;
      total += price.getCurrentPrice();

      if (price.getCurrentPrice() > max) {
        max = price.getCurrentPrice();
      } else if (price.getCurrentPrice() < min) {
        min = price.getCurrentPrice();
      }
    }

    if (count == 0) count = 1;
    if (min == 0L) min = max;

    statsBuilder.setMaxPrice(max);
    statsBuilder.setMinPrice(min);
    statsBuilder.setMeanPrice(total / count);

    statsBuilder.setChartData(getChartData(request.getProductId()));

    return statsBuilder.build();
  }

  private ChartDataMessage getChartData(long productId) {

    List<Price> prices = priceService.findAllByCreatedGreaterThan(productId, new Date(0L));

    List<String> labels =
        prices.stream().map(p -> p.getCreated().toString()).collect(Collectors.toList());

    List<ChartDataSetMessage> dataSets =
        Arrays.asList(
            ChartDataSetMessage.newBuilder()
                .setLabel("current")
                .setFillColor("rgba(220,220,220,0.2)")
                .setStrokeColor("rgba(220,220,220,1)")
                .setPointColor("rgba(220,220,220,1)")
                .setPointStrokeColor("#fff")
                .setPointHighlightFill("#fff")
                .setPointHighlightStroke("rgba(220,220,220,1)")
                .addAllData(
                    prices.stream().map(Price::getCurrentPrice).collect(Collectors.toList()))
                .build(),
            ChartDataSetMessage.newBuilder()
                .setLabel("listed")
                .setFillColor("rgba(151,187,205,0.2)")
                .setStrokeColor("rgba(151,187,205,1)")
                .setPointColor("rgba(151,187,205,1)")
                .setPointStrokeColor("#fff")
                .setPointHighlightFill("#fff")
                .setPointHighlightStroke("rgba(151,187,205,1)")
                .addAllData(prices.stream().map(Price::getListedPrice).collect(Collectors.toList()))
                .build());

    return ChartDataMessage.newBuilder().addAllLabels(labels).addAllDataSets(dataSets).build();
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
