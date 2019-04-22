package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESBrand;
import io.github.benslabbert.trak.search.es.repo.ESBrandRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ESBrandServiceImpl implements ESBrandService {

  private final ESBrandRepo repo;

  @Override
  public Page<ESBrand> findBrandByNameLike(String name, Pageable pageable) {

      if (name.contains(" ")) {
          name = name.split(" ")[0];
      }

    return repo.findAllByNameContaining(name, pageable);
  }
}
