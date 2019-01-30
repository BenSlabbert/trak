package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    if (brand.getName() != null) {
      brand.setName(brand.getName().toUpperCase());
    } else {
      brand.setName("Unknown".toUpperCase());
    }

    try {
      return repo.saveAndFlush(brand);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving brand! Retrying ...");
      return save(brand);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving brand! Retrying ...");
      return save(brand);
    }
  }

  @Override
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
}
