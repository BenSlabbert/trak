package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Price;

import java.util.List;

public interface PriceService {

  Price save(Price price);

  List<Price> findAllByProductId(long productId);
}
