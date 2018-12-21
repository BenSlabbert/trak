package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Product;
import com.trak.entity.jpa.Seller;

public interface ProductService {

  Product save(Product product);

  Iterable<Product> findAll();

  Iterable<Product> findAll(Seller seller);
}
