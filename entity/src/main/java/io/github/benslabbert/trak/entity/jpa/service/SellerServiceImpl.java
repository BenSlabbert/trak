package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
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
public class SellerServiceImpl extends RetryPersist<Seller, Long> implements SellerService {

  private static final String CACHE_NAME = "sellers";

  private final SellerRepo repo;

  public SellerServiceImpl(SellerRepo repo) {
    this.repo = repo;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Seller save(Seller seller) {
    return retry(seller, repo);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'seller-' + #name", unless = "#result == null")
  public Optional<Seller> findByNameEquals(String name) {
    return repo.findByNameEquals(name);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'seller-' + #pageable.pageSize+'-'+#pageable.pageNumber",
      unless = "#result == null")
  public Page<Seller> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'seller-' + #id", unless = "#result == null")
  public Optional<Seller> findById(long id) {
    return repo.findById(id);
  }
}
