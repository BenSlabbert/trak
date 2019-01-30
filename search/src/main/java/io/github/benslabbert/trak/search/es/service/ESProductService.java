package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ESProductService {

  Page<ESProduct> findProductByNameLike(String name, Pageable pageable);
}
