package io.github.benslabbert.trak.entity.jpa.repo;

import io.github.benslabbert.trak.entity.jpa.Price;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepo extends JpaRepository<Price, Long> {

  Page<Price> findAllByProductIdEquals(long productId, Pageable pageable);

  Optional<Price> findTopByProductIdEquals(long productId);

  List<Price> findAllByProductIdEqualsAndCreatedGreaterThanEqual(long productId, Date created);
}
