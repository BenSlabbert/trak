package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Price;
import com.trak.entity.jpa.repo.PriceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class PriceServiceImpl implements PriceService {

  private final PriceRepo repo;

  public PriceServiceImpl(PriceRepo repo) {
    this.repo = repo;
  }

  @Override
  public Price save(Price price) {
    return repo.saveAndFlush(price);
  }

  @Override
  public List<Price> findAllByProductId(long productId) {
    return repo.findAllByProductIdEquals(productId);
  }
}
