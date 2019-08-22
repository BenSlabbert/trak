package io.github.benslabbert.trak.search.grpc;

import io.github.benslabbert.trak.core.grpc.ClientCancelRequest;
import io.github.benslabbert.trak.grpc.SearchRequest;
import io.github.benslabbert.trak.grpc.SearchResponse;
import io.github.benslabbert.trak.grpc.SearchResult;
import io.github.benslabbert.trak.grpc.SearchServiceGrpc;
import io.github.benslabbert.trak.search.es.model.ESSearchResult;
import io.github.benslabbert.trak.search.es.service.ESBrandService;
import io.github.benslabbert.trak.search.es.service.ESCategoryService;
import io.github.benslabbert.trak.search.es.service.ESProductService;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchGRPC extends SearchServiceGrpc.SearchServiceImplBase {

  private final ESCategoryService categoryService;
  private final ESProductService productService;
  private final ESBrandService brandService;

  private static final PageRequest pageable = PageRequest.of(0, 20);

  @Override
  public void brandSearch(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
    SearchResponse response =
        buildSearchResponse(brandService.findBrandByNameLike(request.getSearch(), pageable));

    Deadline deadline = Context.current().getDeadline();
    if (deadline != null && deadline.isExpired()) {
      log.warn("Request took too long to process");
      responseObserver.onCompleted();
      return;
    } else if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      return;
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void categorySearch(
      SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
    SearchResponse response =
        buildSearchResponse(categoryService.findProductByNameLike(request.getSearch(), pageable));

    Deadline deadline = Context.current().getDeadline();
    if (deadline != null && deadline.isExpired()) {
      log.warn("Request took too long to process");
      responseObserver.onCompleted();
      return;
    } else if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      return;
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void productSearch(
      SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
    SearchResponse response =
        buildSearchResponse(productService.findProductByNameLike(request.getSearch(), pageable));

    Deadline deadline = Context.current().getDeadline();
    if (deadline != null && deadline.isExpired()) {
      log.warn("Request took too long to process");
      responseObserver.onCompleted();
      return;
    } else if (Context.current().isCancelled()) {
      responseObserver.onError(ClientCancelRequest.getClientCancelMessage());
      return;
    }

    responseObserver.onNext(response);
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
