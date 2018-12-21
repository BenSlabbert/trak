package com.trak.entity.jpa.repo;

import com.trak.entity.jpa.Product;
import com.trak.entity.jpa.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

  Iterable<Product> findAllBySellerEquals(Seller seller);
}
