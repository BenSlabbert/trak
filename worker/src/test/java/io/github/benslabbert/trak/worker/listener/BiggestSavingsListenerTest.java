package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.repo.*;
import io.github.benslabbert.trak.entity.jpa.service.*;
import io.github.benslabbert.trak.worker.config.JPATestConfig;
import io.github.benslabbert.trak.worker.model.ProductSavings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static io.github.benslabbert.trak.worker.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class BiggestSavingsListenerTest {

  @Autowired private BestSavingsRepo bestSavingsRepo;
  @Autowired private ProductRepo productRepo;
  @Autowired private SellerRepo sellerRepo;
  @Autowired private BrandRepo brandRepo;
  @Autowired private PriceRepo priceRepo;

  @Mock private DistributedLockRegistry lockRegistry;
  @Mock private RabbitTemplate rabbitTemplate;

  private BiggestSavingsListener listener;

  @Before
  public void init() {
    for (int i = 0; i < 10; i++) {
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
            new ProductServiceImpl(
                lockRegistry,
                rabbitTemplate,
                new SellerServiceImpl(sellerRepo, lockRegistry),
                new BrandServiceImpl(lockRegistry, rabbitTemplate, brandRepo),
                productRepo),
            new PriceServiceImpl(priceRepo, lockRegistry));
  }

  @Test
  public void test() {
    listener.processSavingsEvent("uuid");

    List<ProductSavings> savings =
        (List<ProductSavings>) ReflectionTestUtils.getField(listener, "savings");

    assertNotNull(savings);
  }
}
