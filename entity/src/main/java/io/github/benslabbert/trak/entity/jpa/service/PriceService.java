package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;

import java.util.List;
import java.util.Optional;

public interface PriceService {

  Price save(Price price);

  List<Price> findAllByProductId(long productId);

  Optional<Price> findLatestByProductId(long productId);
}
