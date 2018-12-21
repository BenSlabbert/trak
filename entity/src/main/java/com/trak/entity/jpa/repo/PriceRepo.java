package com.trak.entity.jpa.repo;

import com.trak.entity.jpa.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepo extends JpaRepository<Price, Long> {

  List<Price> findAllByProductIdEquals(long productId);
}
