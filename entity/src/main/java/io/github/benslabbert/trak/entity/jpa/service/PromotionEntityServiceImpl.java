package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import io.github.benslabbert.trak.entity.jpa.repo.PromotionEntityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromotionEntityServiceImpl implements PromotionEntityService {

  private final PromotionEntityRepo repo;
  private final ProductService productService;

  @Override
  public PromotionEntity save(String promotionName, Long promotionId, List<Long> plIds) {
    List<Product> products = productService.findAllByPLIDsIn(plIds);
    Optional<PromotionEntity> p = repo.findByTakealotPromotionIdEquals(promotionId);

    if (p.isEmpty()) {
      return repo.saveAndFlush(
          PromotionEntity.builder()
              .name(promotionName)
              .takealotPromotionId(promotionId)
              .products(products)
              .build());
    }

    return p.get();
  }

  @Override
  public Optional<PromotionEntity> findLatest(String promotionName) {
    return repo.findTopByNameEquals(promotionName);
  }
}
