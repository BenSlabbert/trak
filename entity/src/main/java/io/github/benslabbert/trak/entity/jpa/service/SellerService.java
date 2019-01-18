package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Seller;

import java.util.Optional;

public interface SellerService {

  Seller save(Seller seller);

  Optional<Seller> findByNameEquals(String name);

  Iterable<Seller> findAll();

  Optional<Seller> findById(long id);
}
