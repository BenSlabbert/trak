package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Brand;

public interface BrandService {

  Brand save(Brand brand);

  Brand findByNameEquals(String name);
}
