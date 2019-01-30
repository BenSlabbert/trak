package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PriceService {

  Price save(Price price);

  Page<Price> findAllByProductId(long productId, Pageable pageable);

  Optional<Price> findLatestByProductId(long productId);

  List<Price> findAllByCreatedGreaterThan(long productId, Date created);
}
