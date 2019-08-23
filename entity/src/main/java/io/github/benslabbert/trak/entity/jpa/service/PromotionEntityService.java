package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface PromotionEntityService {
  PromotionEntity save(String promotionName, Long promotionId, List<Long> plIds);

  Optional<PromotionEntity> findLatestPromotion(String promotionName);

  Page<PromotionEntity> findLatestPromotion(PageRequest pageRequest);
}
