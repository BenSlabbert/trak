package io.github.benslabbert.trak.search.es.repo;

import io.github.benslabbert.trak.search.es.model.ESBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESBrandRepo extends ElasticsearchRepository<ESBrand, String> {
  Page<ESBrand> findAllByNameContaining(String name, Pageable pageable);
}
