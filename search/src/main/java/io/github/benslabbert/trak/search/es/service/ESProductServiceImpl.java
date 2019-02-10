package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESProduct;
import io.github.benslabbert.trak.search.es.repo.ESProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ESProductServiceImpl implements ESProductService {

  private final ESProductRepo esProductRepo;

  public ESProductServiceImpl(ESProductRepo esProductRepo) {
    this.esProductRepo = esProductRepo;
  }

  @Override
  public Page<ESProduct> findProductByNameLike(String name, Pageable pageable) {
    return esProductRepo.findAllByNameContaining(name, pageable);
  }
}
