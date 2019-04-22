package io.github.benslabbert.trak.entity.jpa.service;

import static io.github.benslabbert.trak.core.cache.CacheNames.CATEGORY_CACHE;

import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.repo.CategoryRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl extends RetryPersist<Category, Long> implements CategoryService {

  private final CategoryRepo repo;

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

    return save(Category.builder().name(name).build());
  }

  @Override
  @Cacheable(value = CATEGORY_CACHE, key = "#id", unless = "#result == null")
  public Optional<Category> findById(long id) {
    return repo.findById(id);
  }

  private Category save(Category category) {
    return retry(category, repo);
  }
}
