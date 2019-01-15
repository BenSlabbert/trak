package com.trak.search.es.service;

import com.trak.search.es.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ESProductService {

  ESProduct addProduct(ESProduct product);

  List<ESProduct> findProductByName(String name);

  List<ESProduct> findProductByNameLike(String name);

  Page<ESProduct> findProductByNameLike(String name, Pageable pageable);
}
