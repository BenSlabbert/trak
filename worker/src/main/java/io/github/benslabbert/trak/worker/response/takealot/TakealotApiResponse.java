package io.github.benslabbert.trak.worker.response.takealot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TakealotApiResponse implements ProductResponse {

  @JsonProperty(value = "buybox")
  private BuyBox buyBox;

  @JsonProperty(value = "data_layer")
  private DataLayer dataLayer;

  @JsonProperty(value = "core")
  private Core core;

  @JsonProperty(value = "sharing")
  private Sharing sharing;

  @JsonProperty(value = "gallery")
  private Gallery gallery;

  @Override
  public Long getCurrentPrice() {

    List<Long> prices = buyBox.getPrices();

    if (prices.isEmpty()) {
      log.error("No prices found in by box!");
      return 0L;
    }

    Long price = prices.get(0);

    return price == null ? 0L : price;
  }

  @Override
  public Long getListedPrice() {
    return buyBox.getListingPrice() == null ? 0L : buyBox.getListingPrice();
  }

  @Override
  public String getSKU() {
    return dataLayer.getSku();
  }

  @Override
  public String getProductName() {
    return core.getTitle();
  }

  @Override
  public String getProductBrand() {
    return core.getBrand();
  }

  @Override
  public List<Author> getAuthors() {
    return core.getAuthors();
  }

  @Override
  public String getProductUrl() {
    return sharing.getUrl();
  }

  @Override
  public Double getRating() {
    return core.getRating();
  }

  @Override
  public List<String> getCategories() {
    return dataLayer.getCategories() != null ? dataLayer.getCategories() : Collections.emptyList();
  }

  @Override
  public List<String> getImageUrls() {

    List<String> images = gallery.getImages();

    for (int i = images.size() - 1; i >= 0; i--) {
      if (images.get(i).contains("{size}")) {
        images.set(i, images.get(i).replace("{size}", "pdpxl"));
      }
    }

    return images;
  }

  @Override
  public boolean isBook() {
    return core.getBrand() == null && !core.getAuthors().isEmpty();
  }
}
