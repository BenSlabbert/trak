package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Brand;

public interface BrandService {

  Brand save(Brand brand);

  Brand findByNameEquals(String name);
}
