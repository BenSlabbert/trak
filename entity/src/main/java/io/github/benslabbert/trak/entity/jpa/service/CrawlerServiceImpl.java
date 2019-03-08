package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.CrawlerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CrawlerServiceImpl extends RetryPersist<Crawler, Long> implements CrawlerService {

  private static final String CACHE_NAME = "crawlers";

  private final CrawlerRepo repo;

  public CrawlerServiceImpl(CrawlerRepo repo) {
    this.repo = repo;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Crawler save(Crawler crawler) {
    return retry(crawler, 1, repo);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'crawler-' + #seller.id", unless = "#result == null")
  public Optional<Crawler> findBySeller(Seller seller) {
    return repo.findBySellerEquals(seller);
  }
}
