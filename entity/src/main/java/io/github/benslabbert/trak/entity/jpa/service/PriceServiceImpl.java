package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PriceServiceImpl extends RetryPersist<Price, Long> implements PriceService {

  private static final String CACHE_NAME = "prices";

  private final PriceRepo repo;

  public PriceServiceImpl(PriceRepo repo) {
    this.repo = repo;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Price save(Price price) {
    return retry(price, 1, repo);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'price-' + #productId + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Price> findAllByProductId(long productId, Pageable pageable) {
    return repo.findAllByProductIdEquals(productId, pageable);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'price-' + #productId", unless = "#result == null")
  public Optional<Price> findLatestByProductId(long productId) {
    return repo.findTopByProductIdEquals(productId);
  }

  @Override
  @Cacheable(
      value = CACHE_NAME,
      key = "'price-' + #productId + '-' + #created.time",
      unless = "#result == null")
  public List<Price> findAllByCreatedGreaterThan(long productId, Date created) {
    return repo.findAllByProductIdEqualsAndCreatedGreaterThanEqual(productId, created);
  }
}
