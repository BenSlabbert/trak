package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
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

    try {
      return repo.saveAndFlush(price);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving price!", e);
      return save(price);
    }
  }

  @Override
  public List<Price> findAllByProductId(long productId) {
    return repo.findAllByProductIdEquals(productId);
  }
}
