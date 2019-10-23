package io.github.benslabbert.trak.search.sonic;

import java.util.List;

public interface SonicSearch {

  List<Long> brand(String text);

  List<Long> product(String text);

  List<Long> category(String text);

  void brandIngest(String id, String text);

  void productIngest(String id, String text);

  void categoryIngest(String id, String text);
}
