package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.BrandService;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbitmq.event.crawler.CrawlerEvent;
import io.github.benslabbert.trak.entity.rabbitmq.event.crawler.CrawlerEventFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
public class CrawlerEventListenerTest {

  @Mock private CategoryService categoryService;
  @Mock private ProductService productService;
  @Mock private PriceService priceService;
  @Mock private BrandService brandService;

  @InjectMocks private CrawlerEventListener listener;

  @Test
  public void testReceive() {

      Seller seller = Seller.builder()
              .id(1L)
              .name("s1")
              .build();

      CrawlerEvent event =
        CrawlerEventFactory.createCrawlerEvent(
            UUID.randomUUID().toString(), seller, 1L);

      listener.receive(event);
  }
}
