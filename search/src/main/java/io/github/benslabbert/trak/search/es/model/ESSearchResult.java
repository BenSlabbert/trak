package io.github.benslabbert.trak.search.es.model;

public interface ESSearchResult {

  String getId();

  String getName();

  Float getScore();
}
