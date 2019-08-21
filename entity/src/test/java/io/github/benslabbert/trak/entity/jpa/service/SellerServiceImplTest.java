package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Seller;
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
public class SellerServiceImplTest {

  @Autowired private SellerRepo repo;

  @Mock private DistributedLockRegistry redisLockRegistry;

  // todo test OptimisticLockException
  private SellerService service;
  private ReentrantLock reentrantLock = new ReentrantLock();

  @Before
  public void setUp() {
    Mockito.when(redisLockRegistry.obtain(Mockito.anyString())).thenReturn(reentrantLock);
    service = new SellerServiceImpl(repo, redisLockRegistry);

    repo.saveAndFlush(Seller.builder().name("seller").build());
  }

  @After
  public void after() {
    assertFalse(reentrantLock.isLocked());
  }

  @Test
  public void saveTest() {
    Seller seller = service.save(Seller.builder().name("s2").build());

    assertNotNull(seller);
    assertEquals("s2", seller.getName());
  }

  @Test
  public void findByNameEqualsTest() {
    Optional<Seller> seller = service.findByNameEquals("seller");

    assertNotNull(seller);
    assertTrue(seller.isPresent());
    assertEquals("seller", seller.get().getName());
  }

  @Test
  public void findByIdTest() {
    Long id = repo.findAll().get(0).getId();

    Optional<Seller> byId = service.findById(id);

    assertNotNull(byId);
    assertTrue(byId.isPresent());
    assertEquals("seller", byId.get().getName());
  }
}
