package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;

import java.util.Optional;

public interface CrawlerService {

  Iterable<Crawler> findAll();

  Crawler save(Crawler crawler);

  Optional<Crawler> findBySeller(Seller seller);
}
