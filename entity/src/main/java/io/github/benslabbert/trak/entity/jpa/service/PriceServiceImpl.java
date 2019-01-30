package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
      log.warn("OptimisticLockException while saving price! Retrying ...");
      return save(price);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving price! Retrying ...");
      return save(price);
    }
  }

  @Override
  public Page<Price> findAllByProductId(long productId, Pageable pageable) {
    return repo.findAllByProductIdEquals(productId, pageable);
  }

  @Override
  public Optional<Price> findLatestByProductId(long productId) {
    return repo.findTopByProductIdEquals(productId);
  }

  @Override
  public List<Price> findAllByCreatedGreaterThan(long productId, Date created) {
    return repo.findAllByProductIdEqualsAndCreatedGreaterThanEqual(productId, created);
  }
}
