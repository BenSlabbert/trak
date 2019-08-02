package io.github.benslabbert.trak.core.model;

public enum Promotion {
  ALL("All"),
  DAILY_DEAL("Daily Deal");

  private String name;

  Promotion(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
