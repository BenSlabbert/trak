package io.github.benslabbert.trak.search.controller;

import io.github.benslabbert.trak.search.es.ESProduct;
import io.github.benslabbert.trak.search.es.service.ESProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ESSearch {

  private final ESProductService service;

  public ESSearch(ESProductService service) {
    this.service = service;
  }

  @GetMapping(path = "/search", produces = "application/json")
  public List<ESProduct> findAll() {

    log.debug("Searching ...");

    ESProduct name1 = service.addProduct(ESProduct.builder().name("name").build());

    List<ESProduct> name = service.findProductByName("am");

    List<ESProduct> name2 = service.findProductByNameLike("am");

    Page<ESProduct> page = service.findProductByNameLike("am", PageRequest.of(0, 10));

    return name2;
  }
}
