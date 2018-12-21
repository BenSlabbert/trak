package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Brand;
import com.trak.entity.jpa.repo.BrandRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

  private final BrandRepo repo;

  public BrandServiceImpl(BrandRepo repo) {
    this.repo = repo;
  }

  @Override
  public Brand save(Brand brand) {
    return repo.saveAndFlush(brand);
  }

  @Override
  public Brand findByNameEquals(String name) {

    if (name == null) {
      return repo.findByNameEquals("Unknown").get();
    }

    name = name.replaceAll("  ", " ").trim().toUpperCase();

    Optional<Brand> brand = repo.findByNameEquals(name);

    if (brand.isEmpty()) {

      try {
        return repo.saveAndFlush(Brand.builder().name(name).build());
      } catch (OptimisticLockException e) {
        log.warn("OptimisticLockException, retrying ...", e);
      }
    }

    return brand.get();
  }
}
