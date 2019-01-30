package io.github.benslabbert.trak.search.grpc;

import io.github.benslabbert.trak.grpc.SearchRequest;
import io.github.benslabbert.trak.grpc.SearchResponse;
import io.github.benslabbert.trak.grpc.SearchServiceGrpc;
import io.github.benslabbert.trak.search.es.ESProduct;
import io.github.benslabbert.trak.search.es.service.ESProductService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class SearchGRPC extends SearchServiceGrpc.SearchServiceImplBase {

  private final ESProductService service;

  public SearchGRPC(ESProductService service) {
    this.service = service;
  }

  @Override
  public void search(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {

    log.debug("Searching for: {}", request.getSearch());

    Page<ESProduct> results =
        service.findProductByNameLike(request.getSearch(), PageRequest.of(0, 10));

    responseObserver.onNext(
        SearchResponse.newBuilder()
            .addAllResults(results.stream().map(ESProduct::getName).collect(Collectors.toList()))
            .build());

    responseObserver.onCompleted();
  }
}
