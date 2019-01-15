package com.trak.worker.response;

import java.util.List;

public interface ProductResponse {

  Long getCurrentPrice();

  Long getListedPrice();

  String getSKU();

  String getProductName();

  String getProductBrand();

  String getProductUrl();

  Double getRating();

  List<String> getCategories();

  List<String> getImageUrls();
}
