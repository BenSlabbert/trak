package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Crawler;
import com.trak.entity.jpa.Seller;

import java.util.Optional;

public interface CrawlerService {

  Iterable<Crawler> findAll();

  Crawler save(Crawler crawler);

  Optional<Crawler> findBySeller(Seller seller);
}
