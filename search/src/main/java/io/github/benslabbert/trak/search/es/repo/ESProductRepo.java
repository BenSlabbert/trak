package io.github.benslabbert.trak.search.es.repo;

import io.github.benslabbert.trak.search.es.model.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESProductRepo extends ElasticsearchRepository<ESProduct, String> {
  Page<ESProduct> findAllByNameContaining(String name, Pageable pageable);
}
