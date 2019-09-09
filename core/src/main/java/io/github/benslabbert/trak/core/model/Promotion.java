package io.github.benslabbert.trak.core.model;

import java.io.Serializable;

public class Promotion implements Serializable {
  public static final String ALL = "All";
  public static final String DAILY_DEAL = "Daily Deal";
  private static final long serialVersionUID = -8880918133264488082L;
  private String name;

  private Promotion(String name) {
    this.name = name;
  }

  public static Promotion create(String name) {
    return new Promotion(name);
  }

  public static Promotion createDailyDeal() {
    return new Promotion(DAILY_DEAL);
  }

  public static Promotion createAll() {
    return new Promotion(ALL);
  }

  public String getName() {
    return name;
  }
}
