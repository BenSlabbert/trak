package com.trak.entity.jpa.service;

import com.trak.entity.config.JPATestConfig;
import com.trak.entity.jpa.Brand;
import com.trak.entity.jpa.repo.BrandRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class BrandServiceImplTest {

  @Autowired private BrandRepo repo;
  private BrandService service;

  @Before
  public void setUp() {
    service = new BrandServiceImpl(repo);
  }

  @Test
  public void saveTest() {

    Brand brand = service.save(Brand.builder().name("brand").build());

    assertNotNull(brand);
    assertEquals("BRAND", brand.getName());
  }

  @Test
  public void findByNameEqualsTest_nullName() {

    Brand brand = service.findByNameEquals(null);

    assertNotNull(brand);
    assertEquals("UNKNOWN", brand.getName());
  }

  @Test
  public void findByNameEqualsTest_notFound() {

    Brand brand = service.findByNameEquals("new brand");

    assertNotNull(brand);
    assertEquals("NEW BRAND", brand.getName());
  }

  @Test
  public void findByNameEqualsTest_found() {

    repo.saveAndFlush(Brand.builder().name("brand").build());

    Brand brand = service.findByNameEquals(" brand ");

    assertNotNull(brand);
    assertEquals("BRAND", brand.getName());
  }
}
