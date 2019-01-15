package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Crawler;
import com.trak.entity.jpa.Seller;
import com.trak.entity.jpa.repo.CrawlerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CrawlerServiceImpl implements CrawlerService {

  private final CrawlerRepo repo;

  public CrawlerServiceImpl(CrawlerRepo repo) {
    this.repo = repo;
  }

  @Override
  public Iterable<Crawler> findAll() {
    return repo.findAll();
  }

  @Override
  public Crawler save(Crawler crawler) {

    try {
      return repo.saveAndFlush(crawler);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving crawler! Retrying ...");
      return save(crawler);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving crawler! Retrying ...");
      return save(crawler);
    }
  }

  @Override
  public Optional<Crawler> findBySeller(Seller seller) {
    return repo.findBySellerEquals(seller);
  }
}
