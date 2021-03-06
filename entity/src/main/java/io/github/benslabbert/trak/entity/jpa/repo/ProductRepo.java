package io.github.benslabbert.trak.entity.jpa.repo;

import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
  Page<Product> findAllByBrandIdEquals(long brandId, Pageable pageable);

  Page<Product> findAllByCategoriesEquals(Category category, Pageable pageable);

  Page<Product> findAllBySellerIdEquals(long sellerId, Pageable pageable);

  List<Product> findAllByPlIdIn(List<Long> plIds);

  Page<Product> findAllByPlIdIn(List<Long> plIds, Pageable pageable);

  Optional<Product> findByPlIdEquals(long plId);
}
