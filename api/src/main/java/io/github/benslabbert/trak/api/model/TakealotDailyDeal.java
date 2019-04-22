package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDeal implements Serializable {

  private TakealotDailyDealResults results;
}
