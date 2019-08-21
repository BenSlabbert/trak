package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import lombok.RequiredArgsConstructor;
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
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.PRICE_CACHE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PriceServiceImpl extends RetryPersist<Price, Long> implements PriceService {

  private final PriceRepo repo;
  private final DistributedLockRegistry lockRegistry;

  @Override
  @CacheEvict(value = PRICE_CACHE, allEntries = true)
  public Price save(Price price) {
    String lockKey = "price-" + price.getProductId();
    Lock lock = lockRegistry.obtain(lockKey);
    log.debug("Obtaining lock: {}", lockKey);
    lock.lock();
    Price p = retry(price, 1, repo);
    log.debug("Releasing lock: {}", lockKey);
    lock.unlock();
    return p;
  }

  @Override
  @Cacheable(
      value = PRICE_CACHE,
      key = "#productId + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Price> findAllByProductId(long productId, Pageable pageable) {
    return repo.findAllByProductIdEquals(productId, pageable);
  }

  @Override
  @Cacheable(value = PRICE_CACHE, key = "#productId", unless = "#result == null")
  public Optional<Price> findLatestByProductId(long productId) {
    return repo.findTopByProductIdEquals(productId);
  }

  @Override
  @Cacheable(
      value = PRICE_CACHE,
      key = "#productId + '-' + #created.time",
      unless = "#result == null")
  public List<Price> findAllByCreatedGreaterThan(long productId, Date created) {
    return repo.findAllByProductIdEqualsAndCreatedGreaterThanEqual(productId, created);
  }

  @Override
  public void delete(long id) {
    if (repo.existsById(id)) repo.deleteById(id);
    else log.warn("No ID for price: {}", id);
  }
}
