package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.repo.CategoryRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class CategoryServiceImplTest {

  @Autowired private CategoryRepo repo;

  @Mock private DistributedLockRegistry redisLockRegistry;
  @Mock private RabbitTemplate rabbitTemplate;

  private CategoryService service;
  private ReentrantLock reentrantLock = new ReentrantLock();

  @Before
  public void setUp() {
    Mockito.when(redisLockRegistry.obtain(Mockito.anyString())).thenReturn(reentrantLock);

    service = new CategoryServiceImpl(redisLockRegistry, rabbitTemplate, repo);

    repo.saveAndFlush(Category.builder().name("CAT").build());
  }

  @After
  public void after() {
    assertFalse(reentrantLock.isLocked());
  }

  @Test
  public void createCategoriesTest() {
    List<Category> categories = service.createCategories(Collections.singletonList("cat"));

    assertNotNull(categories);

    assertEquals(1, categories.size());
    assertEquals("CAT", categories.get(0).getName());
  }

  @Test
  public void createCategoryTest_alreadyExists() {
    Category cat = service.createCategory("cat");

    assertNotNull(cat);
    assertEquals("CAT", cat.getName());
  }

  @Test
  public void createCategoryTest_create() {
    Category cat = service.createCategory("cat2");

    assertNotNull(cat);
    assertEquals("CAT2", cat.getName());
  }

  @Test
  public void findByIdTest_exists() {
    Optional<Category> byId = service.findById(repo.findAll().get(0).getId());
    assertNotNull(byId);
    assertTrue(byId.isPresent());
    assertEquals("CAT", byId.get().getName());
  }

  @Test
  public void findByIdTest_doesNotExist() {
    Optional<Category> byId = service.findById(-1L);
    assertNotNull(byId);
    assertFalse(byId.isPresent());
  }
}
