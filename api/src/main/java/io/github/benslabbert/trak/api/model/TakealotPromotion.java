package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class TakealotPromotion implements Serializable {

  @JsonProperty("response")
  private List<TakealotPromotionsResponse> responseList;
}
