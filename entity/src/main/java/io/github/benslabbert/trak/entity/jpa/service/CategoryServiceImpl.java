package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.repo.CategoryRepo;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.Collection;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.IngestEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.CATEGORY_CACHE;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.SONIC_INGEST_QUEUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl extends RetryPersist<Category, Long> implements CategoryService {

  private final DistributedLockRegistry lockRegistry;
  private final RabbitTemplate rabbitTemplate;
  private final CategoryRepo repo;

  @Override
  public List<Category> createCategories(List<String> names) {
    List<Category> categories = new ArrayList<>();

    for (String name : names) {
      categories.add(createCategory(name));
    }

    return categories;
  }

  @Override
  public Category createCategory(String name) {
    name = name.replace("  ", " ").trim().toUpperCase();

    String lockKey = "category-" + name;
    Lock lock = lockRegistry.obtain(lockKey);
    log.debug("Obtaining lock: {}", lockKey);
    lock.lock();

    try {
      Optional<Category> category = repo.findByNameEquals(name);

      if (category.isPresent()) {
        return category.get();
      }

      Category c = save(Category.builder().name(name).build());
      log.info("Id {} Name {} to be ingested in Sonic", c.getId(), c.getName());
      rabbitTemplate.convertAndSend(
          SONIC_INGEST_QUEUE,
          IngestEventFactory.createIngestEvent(
              Collection.CATEGORY,
              c.getId().toString(),
              c.getName(),
              UUID.randomUUID().toString()));
      return c;
    } finally {
      log.debug("Releasing lock: {}", lockKey);
      lock.unlock();
    }
  }

  @Override
  @Cacheable(value = CATEGORY_CACHE, key = "#id", unless = "#result == null")
  public Optional<Category> findById(long id) {
    return repo.findById(id);
  }

  private Category save(Category category) {
    return retry(category, repo);
  }
}
