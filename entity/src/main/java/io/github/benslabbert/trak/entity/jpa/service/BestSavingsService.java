package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.BestSaving;
import java.util.Collection;
import java.util.List;

public interface BestSavingsService {

  List<BestSaving> saveAll(Collection<BestSaving> bestSavings);

  List<BestSaving> findAll();
}
