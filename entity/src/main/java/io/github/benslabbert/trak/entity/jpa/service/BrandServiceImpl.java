package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BrandServiceImpl extends RetryPersist<Brand, Long> implements BrandService {

  private static final String CACHE_NAME = "brands";

  private final BrandRepo repo;

  public BrandServiceImpl(BrandRepo repo) {
    this.repo = repo;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Brand save(Brand brand) {

    if (brand.getName() != null) {
      brand.setName(brand.getName().toUpperCase());
    } else {
      brand.setName("Unknown".toUpperCase());
    }

    return retry(brand, repo);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'brand-' + #name", unless = "#result == null")
  public Brand findByNameEquals(String name) {

    if (name == null) {
      // see liquibase.sql
      return findByNameEquals("Unknown");
    }

    name = name.replaceAll("  ", " ").trim().toUpperCase();

    Optional<Brand> brand = repo.findByNameEquals(name);

    if (brand.isPresent()) {
      return brand.get();
    }

    try {
      return repo.saveAndFlush(Brand.builder().name(name).build());
    } catch (OptimisticLockException e) {
      return findByNameEquals(name);
    }
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "'brand-' + #id", unless = "#result == null")
  public Optional<Brand> findById(long id) {
    return repo.findById(id);
  }
}
