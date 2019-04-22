package io.github.benslabbert.trak.entity.jpa.service;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
