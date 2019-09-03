package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import io.github.benslabbert.trak.entity.jpa.repo.PromotionEntityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static io.github.benslabbert.trak.core.cache.CacheNames.PROMOTION_ENTITY_CACHE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromotionEntityServiceImpl implements PromotionEntityService {

  private final PromotionEntityRepo repo;
  private final ProductService productService;
  private final DistributedLockRegistry lockRegistry;

  @Override
  @CacheEvict(value = PROMOTION_ENTITY_CACHE, allEntries = true)
  public PromotionEntity save(String promotionName, Long promotionId, List<Long> plIds) {
    log.info("Finding promotion for promotionId: {}", promotionId);
    // todo sometimes returns duplicates
    Optional<PromotionEntity> p = repo.findByTakealotPromotionIdEquals(promotionId);

    if (p.isEmpty()) {
      String lockKey = "promotion-" + promotionId;
      Lock lock = lockRegistry.obtain(lockKey);
      log.debug("Obtaining lock: {}", lockKey);
      lock.lock();
      List<Product> products = productService.findAllByPLIDsIn(plIds);

      try {
        return repo.saveAndFlush(
            PromotionEntity.builder()
                .name(promotionName)
                .takealotPromotionId(promotionId)
                .products(products)
                .build());
      } finally {
        log.debug("Releasing lock: {}", lockKey);
        lock.unlock();
      }
    }

    return p.get();
  }

  @Override
  @Cacheable(value = PROMOTION_ENTITY_CACHE, key = "#promotionName", unless = "#result == null")
  public Optional<PromotionEntity> findLatestPromotion(String promotionName) {
    return repo.findTopByNameEquals(promotionName, Sort.by(Sort.Direction.DESC, "id"));
  }

  @Override
  @Cacheable(value = PROMOTION_ENTITY_CACHE, key = "#id", unless = "#result == null")
  public Optional<PromotionEntity> findById(long id) {
    return repo.findById(id);
  }

  @Override
  @Cacheable(
      value = PROMOTION_ENTITY_CACHE,
      key = "'page:' + #pageRequest.getPageNumber() + ':size:' + #pageRequest.getPageSize()",
      unless = "#result == null")
  public Page<PromotionEntity> findLatestPromotion(PageRequest pageRequest) {
    return repo.findAll(pageRequest);
  }
}
