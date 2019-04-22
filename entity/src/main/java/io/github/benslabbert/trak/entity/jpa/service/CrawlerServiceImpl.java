package io.github.benslabbert.trak.entity.jpa.service;

import static io.github.benslabbert.trak.core.cache.CacheNames.CRAWLER_CACHE;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.CrawlerRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlerServiceImpl extends RetryPersist<Crawler, Long> implements CrawlerService {

  private final CrawlerRepo repo;

  @Override
  @CacheEvict(value = CRAWLER_CACHE, allEntries = true)
  public Crawler save(Crawler crawler) {
    return retry(crawler, 1, repo);
  }

  @Override
  @Cacheable(value = CRAWLER_CACHE, key = "#seller.id", unless = "#result == null")
  public Optional<Crawler> findBySeller(Seller seller) {
    return repo.findBySellerEquals(seller);
  }
}
