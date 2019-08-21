package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.repo.PriceRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class PriceServiceImplTest {

  @Autowired private PriceRepo repo;

  @Mock private DistributedLockRegistry redisLockRegistry;

  private PriceService service;
  private ReentrantLock reentrantLock = new ReentrantLock();

  @Before
  public void setUp() {
    Mockito.when(redisLockRegistry.obtain(Mockito.anyString())).thenReturn(reentrantLock);
    service = new PriceServiceImpl(repo, redisLockRegistry);
    repo.saveAndFlush(Price.builder().productId(1L).build());
  }

  @After
  public void after() {
    repo.deleteAll();
    assertFalse(reentrantLock.isLocked());
  }

  @Test
  public void deleteTest_exists() {
    service.delete(repo.findAll().get(0).getId());
    List<Price> all = repo.findAll();
    assertNotNull(all);
    assertEquals(0, all.size());
  }

  @Test
  public void deleteTest_doesNotExists() {
    service.delete(repo.findAll().get(0).getId() + 1);
    List<Price> all = repo.findAll();
    assertNotNull(all);
    assertEquals(1, all.size());
  }

  @Test
  public void findAllByCreatedGreaterThanTest_found() {
    List<Price> all = service.findAllByCreatedGreaterThan(1L, new Date(0L));
    assertNotNull(all);
    assertEquals(1, all.size());
    assertEquals(1L, all.get(0).getProductId());
  }

  @Test
  public void findAllByCreatedGreaterThanTest_notFound() {
    List<Price> all = service.findAllByCreatedGreaterThan(1L, new Date());
    assertNotNull(all);
    assertEquals(0, all.size());
  }

  @Test
  public void testSaveTest() {
    Price price = service.save(Price.builder().productId(1L).build());

    assertNotNull(price);
    assertEquals(1L, price.getProductId());
  }

  @Test
  public void findAllByProductIdTest() {
    Page<Price> prices = service.findAllByProductId(1L, PageRequest.of(0, 10));

    assertNotNull(prices);
    assertEquals(1, prices.getContent().size());

    assertEquals(1L, prices.getContent().get(0).getProductId());
  }
}
