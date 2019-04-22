package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;
import java.util.Optional;

public interface BrandService {

  Brand save(Brand brand);

  Brand findByNameEquals(String name);

  Optional<Brand> findById(long id);
}
