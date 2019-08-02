package io.github.benslabbert.trak.worker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDealFacet implements Serializable {

  @JsonProperty("name")
  private String name;

  @JsonProperty("entries")
  private List<Object> entries;
}
