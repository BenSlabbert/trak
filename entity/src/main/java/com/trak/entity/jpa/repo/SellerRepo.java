package com.trak.entity.jpa.repo;

import com.trak.entity.jpa.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Long> {

  Optional<Seller> findByNameEquals(String name);
}
