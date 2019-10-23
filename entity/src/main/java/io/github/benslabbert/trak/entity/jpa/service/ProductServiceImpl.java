package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.Collection;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.IngestEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.PRODUCT_CACHE;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.SONIC_INGEST_QUEUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl extends RetryPersist<Product, Long> implements ProductService {

  private final DistributedLockRegistry lockRegistry;
  private final RabbitTemplate rabbitTemplate;
  private final SellerService sellerService;
  private final BrandService brandService;
  private final ProductRepo repo;

  @Override
  @CacheEvict(value = PRODUCT_CACHE, allEntries = true)
  public synchronized Product save(Product product) {
    log.info(
        "Saving product brandId: {} sellerId: {} plId: {}",
        product.getBrandId(),
        product.getSellerId(),
        product.getPlId());

    String lockKey = "product-" + product.getPlId();
    Lock lock = lockRegistry.obtain(lockKey);
    log.debug("Obtaining lock: {}", lockKey);
    lock.lock();

    try {
      Product p = repo.findByPlIdEquals(product.getPlId()).orElseGet(() -> retry(product, 1, repo));
      log.info("Id {} Name {} to be ingested in Sonic", p.getId(), p.getName());
      rabbitTemplate.convertAndSend(
          SONIC_INGEST_QUEUE,
          IngestEventFactory.createIngestEvent(
              Collection.PRODUCT, p.getId().toString(), p.getName(), UUID.randomUUID().toString()));
      return p;
    } finally {
      log.debug("Releasing lock: {}", lockKey);
      lock.unlock();
    }
  }

  @Override
  @Cacheable(value = PRODUCT_CACHE, key = "#id", unless = "#result == null")
  public Optional<Product> findOne(long id) {
    Optional<Product> p = repo.findById(id);

    if (p.isPresent()) {
      Optional<Brand> brand = brandService.findById(p.get().getBrandId());
      brand.ifPresent(value -> p.get().setBrand(value));

      Optional<Seller> seller = sellerService.findById(p.get().getSellerId());
      seller.ifPresent(s -> p.get().setSeller(s));
    }

    return p;
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#pageable.pageSize+'-'+#pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Pageable pageable) {
    return repo.findAll(pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#brand.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Brand brand, Pageable pageable) {
    return repo.findAllByBrandIdEquals(brand.getId(), pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#category.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Category category, Pageable pageable) {
    return repo.findAllByCategoriesEquals(category, pageable);
  }

  @Override
  @Cacheable(
      value = PRODUCT_CACHE,
      key = "#seller.id + '-' + #pageable.pageSize + '-' + #pageable.pageNumber",
      unless = "#result == null")
  public Page<Product> findAll(Seller seller, Pageable pageable) {
    return repo.findAllBySellerIdEquals(seller.getId(), pageable);
  }

  @Override
  @Cacheable(value = PRODUCT_CACHE, unless = "#result == null")
  public List<Product> findAllByPLIDsIn(List<Long> plIds) {
    return repo.findAllByPlIdIn(plIds);
  }

  @Override
  @Cacheable(value = PRODUCT_CACHE, unless = "#result == null")
  public Page<Product> findAllByPLIDsIn(List<Long> plIds, Pageable pageable) {
    return repo.findAllByPlIdIn(plIds, pageable);
  }

  @Override
  public Optional<Product> findByPlID(long plId) {
    return repo.findByPlIdEquals(plId);
  }
}
