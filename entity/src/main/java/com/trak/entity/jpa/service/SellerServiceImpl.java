package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Seller;
import com.trak.entity.jpa.repo.SellerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return repo.saveAndFlush(seller);
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
