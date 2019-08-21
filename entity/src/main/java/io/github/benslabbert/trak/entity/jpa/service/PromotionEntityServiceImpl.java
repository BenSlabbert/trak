package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import io.github.benslabbert.trak.entity.jpa.repo.PromotionEntityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromotionEntityServiceImpl implements PromotionEntityService {

  private final PromotionEntityRepo repo;
  private final ProductService productService;
  private final DistributedLockRegistry lockRegistry;

  @Override
  public PromotionEntity save(String promotionName, Long promotionId, List<Long> plIds) {
    List<Product> products = productService.findAllByPLIDsIn(plIds);
    Optional<PromotionEntity> p = repo.findByTakealotPromotionIdEquals(promotionId);

    if (p.isEmpty()) {
      String lockKey = "promotion-" + promotionId;
      Lock lock = lockRegistry.obtain(lockKey);
      log.debug("Obtaining lock: {}", lockKey);
      lock.lock();

      PromotionEntity pe =
          repo.saveAndFlush(
              PromotionEntity.builder()
                  .name(promotionName)
                  .takealotPromotionId(promotionId)
                  .products(products)
                  .build());
      log.debug("Releasing lock: {}", lockKey);
      lock.unlock();
      return pe;
    }

    return p.get();
  }

  @Override
  public Optional<PromotionEntity> findLatestPromotion(String promotionName) {
    return repo.findTopByNameEquals(promotionName, Sort.by(Sort.Direction.DESC, "id"));
  }
}
