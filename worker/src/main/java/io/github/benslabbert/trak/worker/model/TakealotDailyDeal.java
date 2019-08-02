package io.github.benslabbert.trak.worker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDeal implements Serializable {

  private TakealotDailyDealResults results;
}
