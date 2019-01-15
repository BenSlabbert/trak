package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Product;
import com.trak.entity.jpa.Seller;
import com.trak.entity.jpa.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepo repo;

  public ProductServiceImpl(ProductRepo repo) {
    this.repo = repo;
  }

  @Override
  public Product save(Product product) {

    try {
      return repo.saveAndFlush(product);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving product!", e);
      return save(product);
    }
  }

  @Override
  public Iterable<Product> findAll() {
    return repo.findAll();
  }

  @Override
  public Page<Product> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  public Iterable<Product> findAll(Seller seller) {
    return repo.findAllBySellerEquals(seller);
  }
}
