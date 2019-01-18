package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.jpa.Category;

import java.util.List;

public interface CategoryService {

  List<Category> createCategories(List<String> names);

  Category createCategory(String name);
}
