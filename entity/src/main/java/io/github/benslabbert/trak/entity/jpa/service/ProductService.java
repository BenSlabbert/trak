package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

  Product save(Product product);

  Optional<Product> findOne(long id);

  Page<Product> findAll(Pageable pageable);

  Page<Product> findAll(Brand brand, Pageable pageable);

  Page<Product> findAll(Category category, Pageable pageable);

  Page<Product> findAll(Seller seller, Pageable pageable);

  List<Product> findAllByPLIDsIn(List<Long> plIds);

  Page<Product> findAllByPLIDsIn(List<Long> plIds, Pageable pageable);

  Optional<Product> findByPlID(long plId);
}
