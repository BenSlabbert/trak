package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class TakealotPromotionsResponse implements Serializable {

  @JsonProperty("short_display_name")
  private String displayName;

  @JsonProperty("id")
  private Long promotionId;
}
