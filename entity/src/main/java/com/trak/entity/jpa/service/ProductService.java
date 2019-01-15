package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Product;
import com.trak.entity.jpa.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Product save(Product product);

  Iterable<Product> findAll();

  Page<Product> findAll(Pageable pageable);

  Iterable<Product> findAll(Seller seller);
}
