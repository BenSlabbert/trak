package io.github.benslabbert.trak.entity.jpa.repo;

import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionEntityRepo extends JpaRepository<PromotionEntity, Long> {
  Optional<PromotionEntity> findByTakealotPromotionIdEquals(long id);

  Optional<PromotionEntity> findTopByNameEquals(String name, Sort sort);
}
