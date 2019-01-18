package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

  private final SellerRepo repo;

  public SellerServiceImpl(SellerRepo repo) {
    this.repo = repo;
  }

  @Override
  public Seller save(Seller seller) {

    try {
      return repo.saveAndFlush(seller);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving seller!", e);
      return save(seller);
    }
  }

  @Override
  public Optional<Seller> findByNameEquals(String name) {
    return repo.findByNameEquals(name);
  }

  @Override
  public Iterable<Seller> findAll() {
    return repo.findAll();
  }

  @Override
  public Optional<Seller> findById(long id) {
    return repo.findById(id);
  }
}
