package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ESBrandService {

    Page<ESBrand> findBrandByNameLike(String name, Pageable pageable);
}
