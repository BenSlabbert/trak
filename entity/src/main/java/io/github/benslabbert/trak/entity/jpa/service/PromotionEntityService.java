package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.PromotionEntity;

import java.util.List;
import java.util.Optional;

public interface PromotionEntityService {
  PromotionEntity save(String promotionName, Long promotionId, List<Long> plIds);

  Optional<PromotionEntity> findLatest(String promotionName);
}
