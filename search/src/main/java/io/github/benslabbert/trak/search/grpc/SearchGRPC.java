package io.github.benslabbert.trak.search.grpc;

import io.github.benslabbert.trak.grpc.SearchRequest;
import io.github.benslabbert.trak.grpc.SearchResponse;
import io.github.benslabbert.trak.grpc.SearchResult;
import io.github.benslabbert.trak.grpc.SearchServiceGrpc;
import io.github.benslabbert.trak.search.es.model.ESSearchResult;
import io.github.benslabbert.trak.search.es.service.ESBrandService;
import io.github.benslabbert.trak.search.es.service.ESCategoryService;
import io.github.benslabbert.trak.search.es.service.ESProductService;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SearchGRPC extends SearchServiceGrpc.SearchServiceImplBase {

  private final ESCategoryService categoryService;
  private final ESProductService productService;
  private final ESBrandService brandService;

  private static final PageRequest pageable = PageRequest.of(0, 20);

  public SearchGRPC(
      ESCategoryService categoryService,
      ESProductService productService,
      ESBrandService brandService) {

    this.categoryService = categoryService;
    this.productService = productService;
    this.brandService = brandService;
  }

  @Override
  public void brandSearch(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {

    log.debug("Searching for brand: {}", request.getSearch());

    responseObserver.onNext(
        buildSearchResponse(brandService.findBrandByNameLike(request.getSearch(), pageable)));

    responseObserver.onCompleted();
  }

  @Override
  public void categorySearch(
      SearchRequest request, StreamObserver<SearchResponse> responseObserver) {

    log.debug("Searching for category: {}", request.getSearch());

    responseObserver.onNext(
        buildSearchResponse(categoryService.findProductByNameLike(request.getSearch(), pageable)));

    responseObserver.onCompleted();
  }

  @Override
  public void productSearch(
      SearchRequest request, StreamObserver<SearchResponse> responseObserver) {

    log.debug("Searching for product: {}", request.getSearch());

    responseObserver.onNext(
        buildSearchResponse(productService.findProductByNameLike(request.getSearch(), pageable)));

    responseObserver.onCompleted();
  }

  private SearchResponse buildSearchResponse(Page<? extends ESSearchResult> results) {
    return SearchResponse.newBuilder()
        .addAllResults(buildSearchResults(results.getContent()))
        .build();
  }

  private List<SearchResult> buildSearchResults(List<? extends ESSearchResult> results) {
    return results.stream()
        .map(
            p ->
                SearchResult.newBuilder()
                    .setId(p.getId())
                    .setName(p.getName())
                    .setScore(p.getScore())
                    .build())
        .collect(Collectors.toList());
  }
}
