package io.github.benslabbert.trak.search.es.repo;

import io.github.benslabbert.trak.search.es.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ESProductRepo extends ElasticsearchRepository<ESProduct, String> {

  List<ESProduct> findAllByNameEquals(String name);

  Page<ESProduct> findAllByNameIgnoreCase(String name, Pageable pageable);

  List<ESProduct> findAllByNameContaining(String name);
}
