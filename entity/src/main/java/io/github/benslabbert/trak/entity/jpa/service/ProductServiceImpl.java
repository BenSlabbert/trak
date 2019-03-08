package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

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
      log.warn("OptimisticLockException while saving product! Retrying ...");
      return save(product);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving product! Retrying ...");
      return save(product);
    }
  }

  @Override
  public Optional<Product> findOne(long id) {
    return repo.findById(id);
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
  public Page<Product> findAll(Brand brand, Pageable pageable) {
    return repo.findAllByBrandEquals(brand, pageable);
  }

  @Override
  public Page<Product> findAll(Seller seller, Pageable pageable) {
    return repo.findAllBySellerEquals(seller, pageable);
  }
}
