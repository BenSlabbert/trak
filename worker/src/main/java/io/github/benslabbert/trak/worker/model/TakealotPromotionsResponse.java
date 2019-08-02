package io.github.benslabbert.trak.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TakealotPromotionsResponse implements Serializable {

  @JsonProperty("short_display_name")
  private String displayName;

  @JsonProperty("id")
  private Long promotionId;
}
