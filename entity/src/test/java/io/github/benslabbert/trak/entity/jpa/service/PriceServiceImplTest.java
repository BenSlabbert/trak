package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class PriceServiceImplTest {

  @Autowired private PriceRepo repo;

  private PriceService service;

  @Before
  public void setUp() {

    service = new PriceServiceImpl(repo);

    repo.saveAndFlush(Price.builder().productId(1L).build());
  }

  @Test
  public void testSave() {

    Price price = service.save(Price.builder().productId(1L).build());

    assertNotNull(price);
    assertEquals(1L, price.getProductId());
  }

  @Test
  public void findAllByProductIdTest() {

    List<Price> prices = service.findAllByProductId(1L);

    assertNotNull(prices);
    assertEquals(1, prices.size());

    assertEquals(1L, prices.get(0).getProductId());
  }
}
