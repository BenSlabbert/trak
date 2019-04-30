package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static io.github.benslabbert.trak.core.cache.CacheNames.PRODUCT_CACHE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl extends RetryPersist<Product, Long> implements ProductService {

  private final ProductRepo repo;

  @Override
  @CacheEvict(value = PRODUCT_CACHE, allEntries = true)
  public Product save(Product product) {
    return retry(product, 1, repo);
  }

  @Override
  @Cacheable(value = PRODUCT_CACHE, key = "#id", unless = "#result == null")
  public Optional<Product> findOne(long id) {
    return repo.findById(id);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#pageable.pageSize+'-'+#pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#brand.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Brand brand, Pageable pageable) {
    return repo.findAllByBrandEquals(brand, pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#category.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Category category, Pageable pageable) {
    return repo.findAllByCategoriesEquals(category, pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#seller.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Seller seller, Pageable pageable) {
    return repo.findAllBySellerEquals(seller, pageable);
  }

  @Override
  @Cacheable(value = PRODUCT_CACHE, key = "#plIds.toString()", unless = "#result == null")
  public List<Product> findAllByPLIDsIn(List<Long> plIds) {
    return repo.findAllByPlIdIn(plIds);
  }

  @Override
  public Optional<Product> findByPlID(long plId) {
    return repo.findByPlIdEquals(plId);
  }
}
