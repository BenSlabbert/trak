package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.SELLER_CACHE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SellerServiceImpl extends RetryPersist<Seller, Long> implements SellerService {

  private final SellerRepo repo;
  private final DistributedLockRegistry lockRegistry;

  @Override
  @CacheEvict(value = SELLER_CACHE, allEntries = true)
  public Seller save(Seller seller) {
    String lockKey = "seller-" + seller.getName();
    Lock lock = lockRegistry.obtain(lockKey);
    log.debug("Obtaining lock: {}", lockKey);
    lock.lock();

    try {
      return retry(seller, repo);
    } finally {
      log.debug("Releasing lock: {}", lockKey);
      lock.unlock();
    }
  }

  @Override
  @Cacheable(value = SELLER_CACHE, key = "#name", unless = "#result == null")
  public Optional<Seller> findByNameEquals(String name) {
    return repo.findByNameEquals(name);
  }

  @Override
  @Cacheable(
      value = SELLER_CACHE,
      key = "#pageable.pageSize+'-'+#pageable.pageNumber",
      unless = "#result == null")
  public Page<Seller> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  @Cacheable(value = SELLER_CACHE, key = "#id", unless = "#result == null")
  public Optional<Seller> findById(long id) {
    return repo.findById(id);
  }
}
