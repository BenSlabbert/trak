package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

import static io.github.benslabbert.trak.core.cache.CacheNames.BRAND_CACHE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BrandServiceImpl extends RetryPersist<Brand, Long> implements BrandService {

  private final BrandRepo repo;

  @Override
  @CacheEvict(value = BRAND_CACHE, allEntries = true)
  public Brand save(Brand brand) {

    if (brand.getName() != null) {
      brand.setName(brand.getName().toUpperCase());
    } else {
      brand.setName("Unknown".toUpperCase());
    }

    return retry(brand, repo);
  }

  @Override
  @Cacheable(
          value = BRAND_CACHE,
          key = "#name",
          unless = "#result == null || #name == null",
          condition = "#name != null")
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
  @Cacheable(value = BRAND_CACHE, key = "#id", unless = "#result == null")
  public Optional<Brand> findById(long id) {
    return repo.findById(id);
  }
}
