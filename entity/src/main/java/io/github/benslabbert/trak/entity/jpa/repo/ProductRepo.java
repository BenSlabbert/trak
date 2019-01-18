package io.github.benslabbert.trak.entity.jpa.repo;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

  Iterable<Product> findAllBySellerEquals(Seller seller);
}
