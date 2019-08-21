package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.CrawlerRepo;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class CrawlerServiceImplTest {

  @Autowired private CrawlerRepo crawlerRepo;
  @Autowired private SellerRepo sellerRepo;

  @Mock private DistributedLockRegistry redisLockRegistry;

  // todo test OptimisticLockException
  private CrawlerService service;
  private ReentrantLock reentrantLock = new ReentrantLock();

  @Before
  public void setUp() {
    Mockito.when(redisLockRegistry.obtain(Mockito.anyString())).thenReturn(reentrantLock);

    service = new CrawlerServiceImpl(crawlerRepo, redisLockRegistry);

    Seller seller = sellerRepo.saveAndFlush(Seller.builder().name("seller").build());
    crawlerRepo.saveAndFlush(Crawler.builder().lastId(1L).seller(seller).build());
  }

  @After
  public void after() {
    assertFalse(reentrantLock.isLocked());
  }

  @Test
  public void saveTest() {
    Crawler crawler = crawlerRepo.findAll().get(0);

    assertNotNull(crawler);

    crawler.setLastId(2L);

    crawler = service.save(crawler);

    assertNotNull(crawler);

    assertEquals(2L, crawler.getLastId().longValue());
  }

  @Test
  public void findBySellerTest() {
    Seller seller = sellerRepo.findAll().get(0);

    Optional<Crawler> crawler = service.findBySeller(seller);

    assertNotNull(crawler);
    assertTrue(crawler.isPresent());
    assertNotNull(crawler.get().getSeller());

    assertEquals(1L, crawler.get().getLastId().longValue());
    assertEquals("seller", crawler.get().getSeller().getName());
  }
}
