package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;

import java.util.List;

public interface PriceService {

  Price save(Price price);

  List<Price> findAllByProductId(long productId);
}
