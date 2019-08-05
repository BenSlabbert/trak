package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbitmq.event.price.clean.PriceCleanUpEventFactory;
import io.github.benslabbert.trak.worker.config.JPATestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.github.benslabbert.trak.worker.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class PriceCleanupEventListenerTest {

  @Autowired private ProductService productService;
  @Autowired private PriceService priceService;

  private PriceCleanupEventListener listener;

  @Before
  public void init() {
    listener = new PriceCleanupEventListener(productService, priceService);
    assertNotNull(listener);
  }

  @Test
  public void test_allEqual() {
    Product product =
        productService.save(Product.builder().plId(1L).brandId(1L).sellerId(1L).build());

    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());

    listener.receive(PriceCleanUpEventFactory.create(product.getId()));

    Page<Price> all = priceService.findAllByProductId(product.getId(), PageRequest.of(0, 100));
    assertNotNull(all);
    assertEquals(1, all.getContent().size());
  }

  @Test
  public void test_notAllEqual() {
    Product product =
        productService.save(Product.builder().plId(1L).brandId(1L).sellerId(1L).build());

    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(2L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(2L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());
    priceService.save(
        Price.builder().currentPrice(1L).listedPrice(1L).productId(product.getId()).build());

    listener.receive(PriceCleanUpEventFactory.create(product.getId()));

    Page<Price> all = priceService.findAllByProductId(product.getId(), PageRequest.of(0, 100));
    assertNotNull(all);

    List<Price> content = all.getContent();
    assertEquals(3, content.size());

    assertEquals(1L, content.get(0).getCurrentPrice());
    assertEquals(1L, content.get(0).getListedPrice());

    assertEquals(2L, content.get(1).getCurrentPrice());
    assertEquals(1L, content.get(1).getListedPrice());

    assertEquals(1L, content.get(2).getCurrentPrice());
    assertEquals(2L, content.get(2).getListedPrice());
  }
}
