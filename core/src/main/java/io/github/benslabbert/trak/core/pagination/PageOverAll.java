package io.github.benslabbert.trak.core.pagination;

import org.springframework.data.domain.Page;

public abstract class PageOverAll<T> {

  protected void pageOverAll(Page<T> page) {

    while (page != null && page.hasContent()) {

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

  protected abstract Page<T> nextPage(Page<T> page);

  protected abstract void processItem(T item);
}
