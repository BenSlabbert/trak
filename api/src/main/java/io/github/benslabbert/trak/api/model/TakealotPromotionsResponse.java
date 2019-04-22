package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class TakealotPromotionsResponse implements Serializable {

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("promotion_id")
  private Long promotionId;
}
