package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.repo.CategoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepo repo;

  public CategoryServiceImpl(CategoryRepo repo) {
    this.repo = repo;
  }

  @Override
  public List<Category> createCategories(List<String> names) {

    List<Category> categories = new ArrayList<>();

    for (String name : names) {
      categories.add(createCategory(name));
    }

    return categories;
  }

  @Override
  public Category createCategory(String name) {

    name = name.replaceAll("  ", " ").trim().toUpperCase();

    Optional<Category> category = repo.findByNameEquals(name);

    if (category.isPresent()) return category.get();

    return repo.saveAndFlush(Category.builder().name(name).build());
  }
}
