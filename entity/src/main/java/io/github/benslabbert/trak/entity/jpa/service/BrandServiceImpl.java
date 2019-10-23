package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.Collection;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.IngestEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.BRAND_CACHE;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.SONIC_INGEST_QUEUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BrandServiceImpl extends RetryPersist<Brand, Long> implements BrandService {

  private final DistributedLockRegistry lockRegistry;
  private final RabbitTemplate rabbitTemplate;
  private final BrandRepo repo;

  @Override
  @CacheEvict(value = BRAND_CACHE, allEntries = true)
  public Brand save(Brand brand) {
    log.info("Saving brand: {}", brand);

    if (brand.getName() != null) {
      brand.setName(brand.getName().toUpperCase());
    } else {
      brand.setName("Unknown".toUpperCase());
    }

    Brand b = retry(brand, repo);
    log.info("Id {} Name {} to be ingested in Sonic", b.getId(), b.getName());
    rabbitTemplate.convertAndSend(
        SONIC_INGEST_QUEUE,
        IngestEventFactory.createIngestEvent(
            Collection.BRAND, b.getId().toString(), b.getName(), UUID.randomUUID().toString()));
    return b;
  }

  /**
   * This method does an upsert on the brand name and caches it. We therefore lock on the name when
   * the method executes (i.e. not cached)
   *
   * @param name unique name of the brand
   * @return Brand
   */
  @Override
  @Cacheable(
      value = BRAND_CACHE,
      key = "#name",
      unless = "#result == null || #name == null",
      condition = "#name != null")
  public Brand findByNameEquals(String name) {
    log.info("Looking for brand with name: {}", name);

    if (name == null) {
      // see liquibase.sql
      return findByNameEquals("Unknown");
    }

    name = name.replace("  ", " ").trim().toUpperCase();

    String lockKey = "brand-" + name;
    Lock lock = lockRegistry.obtain(lockKey);
    log.debug("Obtaining lock: {}", lockKey);
    lock.lock(); // lock is released in the finally

    try {
      Optional<Brand> brand = repo.findByNameEquals(name);

      if (brand.isPresent()) {
        log.info("Found brand with name: {}", name);
        return brand.get();
      }

      log.info("Creating brand with name: {}", name);
      return save(Brand.builder().name(name).build());
    } catch (OptimisticLockException e) {
      return findByNameEquals(name);
    } finally {
      log.debug("Releasing lock: {}", lockKey);
      lock.unlock();
    }
  }

  @Override
  @Cacheable(value = BRAND_CACHE, key = "#id", unless = "#result == null")
  public Optional<Brand> findById(long id) {
    return repo.findById(id);
  }
}
