package io.github.benslabbert.trak.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionIds {

  private String name;
  private long promotionId;
  private List<Long> plIDs;
}
