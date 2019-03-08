package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl extends RetryPersist<Product, Long> implements ProductService {

  private static final String CACHE_NAME = "products";

  private final ProductRepo repo;

  public ProductServiceImpl(ProductRepo repo) {
    this.repo = repo;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Product save(Product product) {
    return retry(product, 1, repo);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'product-' + #id", unless = "#result == null")
  public Optional<Product> findOne(long id) {
    return repo.findById(id);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'product-' + #pageable.pageSize+'-'+#pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'product-' + #brand.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Brand brand, Pageable pageable) {
    return repo.findAllByBrandEquals(brand, pageable);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'product-' + #category.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Category category, Pageable pageable) {
    return repo.findAllByCategoriesEquals(category, pageable);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'product-' + #seller.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Seller seller, Pageable pageable) {
    return repo.findAllBySellerEquals(seller, pageable);
  }
}
