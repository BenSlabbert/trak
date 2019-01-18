package io.github.benslabbert.trak.api.latest;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LatestItems {

  private final ProductService productService;

  public LatestItems(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping(path = "/latest")
  public Page<Product> getLatestProducts() {

    log.debug("Getting latest products");

    return productService.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));
  }
}
