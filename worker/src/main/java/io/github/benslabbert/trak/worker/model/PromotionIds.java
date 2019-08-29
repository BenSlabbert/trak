package io.github.benslabbert.trak.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionIds implements Serializable {

  private static final long serialVersionUID = -8426539473735678638L;

  private String name;
  private long promotionId;
  private List<Long> plIDs;
}
