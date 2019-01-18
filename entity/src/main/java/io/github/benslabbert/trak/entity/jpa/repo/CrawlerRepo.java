package io.github.benslabbert.trak.entity.jpa.repo;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrawlerRepo extends JpaRepository<Crawler, Long> {

  Optional<Crawler> findBySellerEquals(Seller seller);
}
