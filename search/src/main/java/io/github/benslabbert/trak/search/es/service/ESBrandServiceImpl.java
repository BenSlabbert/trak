package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESBrand;
import io.github.benslabbert.trak.search.es.repo.ESBrandRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ESBrandServiceImpl implements ESBrandService {

  private final ESBrandRepo repo;

  public ESBrandServiceImpl(ESBrandRepo repo) {
    this.repo = repo;
  }

  @Override
  public Page<ESBrand> findProductByNameLike(String name, Pageable pageable) {
    return repo.findAllByNameContaining(name, pageable);
  }
}
