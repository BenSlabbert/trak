package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ESCategoryService {
  Page<ESCategory> findProductByNameLike(String name, Pageable pageable);
}
