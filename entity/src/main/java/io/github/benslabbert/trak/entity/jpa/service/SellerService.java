package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SellerService {

  Seller save(Seller seller);

  Optional<Seller> findByNameEquals(String name);

  Page<Seller> findAll(Pageable pageable);

  Optional<Seller> findById(long id);
}
