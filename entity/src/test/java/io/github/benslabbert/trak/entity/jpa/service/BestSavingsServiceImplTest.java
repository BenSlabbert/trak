package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.BestSaving;
import io.github.benslabbert.trak.entity.jpa.repo.BestSavingsRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class BestSavingsServiceImplTest {

  @Autowired private BestSavingsRepo repo;

  private BestSavingsService service;

  @Before
  public void setUp() {
    service = new BestSavingsServiceImpl(repo);
  }

  @Test
  public void annotations() throws NoSuchMethodException {

    Method method = service.getClass().getMethod("saveAll", Collection.class);
    CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
    assertNotNull(cacheEvict);

    method = service.getClass().getMethod("findAll");
    Cacheable cacheable = method.getAnnotation(Cacheable.class);
    assertNotNull(cacheable);
  }

  @Test
  public void saveAllTest() {

    List<BestSaving> savings =
        service.saveAll(Collections.singletonList(BestSaving.builder().build()));
    assertNotNull(savings);
    assertEquals(1, savings.size());
  }

  @Test
  public void findAllTest() {

    service.saveAll(Collections.singletonList(BestSaving.builder().build()));

    List<BestSaving> savings = service.findAll();
    assertNotNull(savings);
    assertEquals(1, savings.size());
  }
}
