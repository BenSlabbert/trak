package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.BestSaving;
import io.github.benslabbert.trak.entity.jpa.repo.BestSavingsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static io.github.benslabbert.trak.core.cache.CacheNames.BEST_SAVINGS_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestSavingsServiceImpl implements BestSavingsService {

  private final BestSavingsRepo repo;

  @Override
  @CacheEvict(value = BEST_SAVINGS_CACHE, key = "'best-savings'")
  public List<BestSaving> saveAll(Collection<BestSaving> bestSavings) {
    return repo.saveAll(bestSavings);
  }

  @Override
  @Cacheable(value = BEST_SAVINGS_CACHE, key = "'best-savings'", unless = "#result == null")
  public List<BestSaving> findAll() {
    return repo.findAll();
  }
}
