package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.ESProduct;
import io.github.benslabbert.trak.search.es.repo.ESProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ESProductServiceImpl implements ESProductService {

  private final ESProductRepo esProductRepo;

  public ESProductServiceImpl(ESProductRepo esProductRepo) {
    this.esProductRepo = esProductRepo;
  }

  @Override
  public ESProduct addProduct(ESProduct product) {
    return esProductRepo.save(product);
  }

  @Override
  public List<ESProduct> findProductByName(String name) {
    return esProductRepo.findAllByNameEquals(name);
  }

  @Override
  public List<ESProduct> findProductByNameLike(String name) {
    return esProductRepo.findAllByNameContaining(name);
  }

  @Override
  public Page<ESProduct> findProductByNameLike(String name, Pageable pageable) {
    return esProductRepo.findAllByNameContaining(name, pageable);
  }
}
