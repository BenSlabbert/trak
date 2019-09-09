package io.github.benslabbert.trak.core.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public abstract class PageOverContent<T> {

  protected void pageOverContent(Page<T> page) {

    while (page.hasContent()) {

      processContent(page.getContent());

      if (page.hasNext()) {
        page = nextPage(page);
      } else {
        break;
      }
    }
  }

  protected abstract Page<T> nextPage(Page<T> page);

  protected abstract void processContent(List<T> content);
}
