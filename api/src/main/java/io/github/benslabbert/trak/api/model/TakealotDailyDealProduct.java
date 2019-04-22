package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDealProduct implements Serializable {

  @JsonProperty("id")
  private Long plId;
}
