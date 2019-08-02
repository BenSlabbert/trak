package io.github.benslabbert.trak.worker.response;

import io.github.benslabbert.trak.worker.response.takealot.Author;

import java.util.List;

public interface ProductResponse {

  Long getCurrentPrice();

  Long getListedPrice();

  String getSKU();

  String getProductName();

  String getProductBrand();

  List<Author> getAuthors();

  String getProductUrl();

  Double getRating();

  List<String> getCategories();

  List<String> getImageUrls();

  boolean isBook();
}
