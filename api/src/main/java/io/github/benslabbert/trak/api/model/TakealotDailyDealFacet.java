package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDealFacet implements Serializable {

  @JsonProperty("name")
  private String name;

  @JsonProperty("entries")
  private List<Object> entries;
}
