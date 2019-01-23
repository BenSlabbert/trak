package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

  Product save(Product product);

  Optional<Product> findOne(long id);

  Iterable<Product> findAll();

  Page<Product> findAll(Pageable pageable);

  Iterable<Product> findAll(Seller seller);
}
