package io.github.benslabbert.trak.engine.job;

import org.springframework.data.domain.Page;

abstract class PageOverAll<T> {

  void pageOverAll(Page<T> page) {

    while (page.hasContent()) {

      for (T t : page.getContent()) {
        processItem(t);
      }

      if (page.hasNext()) {
        page = nextPage(page);
      } else {
        break;
      }
    }
  }

  abstract Page<T> nextPage(Page<T> page);

  abstract void processItem(T item);
}
