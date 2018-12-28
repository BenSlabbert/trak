package com.trak.entity.jpa.service;

import com.trak.entity.config.JPATestConfig;
import com.trak.entity.jpa.Category;
import com.trak.entity.jpa.repo.CategoryRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class CategoryServiceImplTest {

  @Autowired private CategoryRepo repo;

  private CategoryService service;

  @Before
  public void setUp() {

    service = new CategoryServiceImpl(repo);

    repo.saveAndFlush(Category.builder().name("CAT").build());
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

    Category cat = service.createCategory("cat");

    assertNotNull(cat);
    assertEquals("CAT", cat.getName());
  }
}
