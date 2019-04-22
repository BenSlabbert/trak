package io.github.benslabbert.trak.worker.response.takealot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class BuyBox {

  @JsonProperty(value = "product_line_id")
  private String productLineId;

  @JsonProperty(value = "listing_price")
  private Long listingPrice;

  @JsonProperty(value = "prices")
  private List<Long> prices;
}
