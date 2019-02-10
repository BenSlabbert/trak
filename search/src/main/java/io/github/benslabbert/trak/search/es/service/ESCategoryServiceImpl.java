package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESCategory;
import io.github.benslabbert.trak.search.es.repo.ESCategoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ESCategoryServiceImpl implements ESCategoryService {

  private final ESCategoryRepo repo;

  public ESCategoryServiceImpl(ESCategoryRepo repo) {
    this.repo = repo;
  }

  @Override
  public Page<ESCategory> findProductByNameLike(String name, Pageable pageable) {
    return repo.findAllByNameContaining(name, pageable);
  }
}
