package io.github.benslabbert.trak.search.sonic;

import java.util.List;

public interface Search {

  List<Long> brand(String text);

  List<Long> product(String text);

  List<Long> category(String text);
}
