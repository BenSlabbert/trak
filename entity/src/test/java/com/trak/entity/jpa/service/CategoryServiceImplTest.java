package com.trak.entity.jpa.service;

import com.trak.entity.jpa.Category;
import com.trak.entity.jpa.repo.CategoryRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CategoryServiceImplTest {

  @MockBean private CategoryRepo repo;

  private CategoryService service;

  @Before
  public void setUp() {
    service = new CategoryServiceImpl(repo);
  }

  @Test
  public void createCategoriesTest() {

    given(repo.findByNameEquals("cat".toUpperCase()))
        .willReturn(Optional.of(Category.builder().name("CAT").build()));

    List<Category> categories = service.createCategories(Collections.singletonList("cat"));

    assertNotNull(categories);

    assertEquals(1, categories.size());
    assertEquals("CAT", categories.get(0).getName());
  }

  @Test
  public void createCategoryTest_alreadyExists() {

    given(repo.findByNameEquals("cat".toUpperCase()))
        .willReturn(Optional.of(Category.builder().name("CAT").build()));

    Category cat = service.createCategory("cat");

    assertNotNull(cat);
    assertEquals("CAT", cat.getName());
  }

  @Test
  public void createCategoryTest_create() {

    given(repo.findByNameEquals("cat".toUpperCase())).willReturn(Optional.empty());
    given(repo.saveAndFlush(any())).willReturn(Category.builder().name("CAT").build());

    Category cat = service.createCategory("cat");

    assertNotNull(cat);
    assertEquals("CAT", cat.getName());
  }
}
