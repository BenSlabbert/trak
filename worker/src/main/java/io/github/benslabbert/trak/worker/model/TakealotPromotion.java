package io.github.benslabbert.trak.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TakealotPromotion implements Serializable {

  private static final long serialVersionUID = -7800465926541195146L;

  @JsonProperty("response")
  private List<TakealotPromotionsResponse> responseList;
}
