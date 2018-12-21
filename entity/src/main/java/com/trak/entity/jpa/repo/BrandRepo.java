package com.trak.entity.jpa.repo;

import com.trak.entity.jpa.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Long> {

  Optional<Brand> findByNameEquals(String name);
}
