package io.github.benslabbert.trak.worker.listener;

import static io.github.benslabbert.trak.worker.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertNotNull;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.repo.BestSavingsRepo;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import io.github.benslabbert.trak.entity.jpa.service.BestSavingsServiceImpl;
import io.github.benslabbert.trak.entity.jpa.service.PriceServiceImpl;
import io.github.benslabbert.trak.entity.jpa.service.ProductServiceImpl;
import io.github.benslabbert.trak.worker.config.JPATestConfig;
import io.github.benslabbert.trak.worker.model.ProductSavings;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class BiggestSavingsListenerTest {

  @Autowired private BestSavingsRepo bestSavingsRepo;
  @Autowired private ProductRepo productRepo;
  @Autowired private PriceRepo priceRepo;

  private BiggestSavingsListener listener;

  @Before
  public void init() {

    for (int i = 0; i < 1000; i++) {
      Product p = productRepo.saveAndFlush(Product.builder().name("p" + i).plId((long) i).build());
      int listedPrice = new Random().nextInt(100);
      int currentPrice = listedPrice - new Random().nextInt(20);
      priceRepo.saveAndFlush(
          Price.builder()
              .productId(p.getId())
              .listedPrice(listedPrice)
              .currentPrice(currentPrice)
              .build());
    }

    listener =
        new BiggestSavingsListener(
            new BestSavingsServiceImpl(bestSavingsRepo),
            new ProductServiceImpl(productRepo),
            new PriceServiceImpl(priceRepo));
  }

  @Test
  @Ignore("fix me")
  public void test() {

    listener.processSavingsEvent("uuid");

    List<ProductSavings> tree =
        (List<ProductSavings>) ReflectionTestUtils.getField(listener, "savings");

    assertNotNull(tree);
  }
}
