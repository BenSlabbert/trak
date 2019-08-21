package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Brand;
import io.github.benslabbert.trak.entity.jpa.Category;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.BrandRepo;
import io.github.benslabbert.trak.entity.jpa.repo.CategoryRepo;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
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

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class ProductServiceImplTest {

  @Autowired private CategoryRepo categoryRepo;
  @Autowired private SellerRepo sellerRepo;
  @Autowired private BrandRepo brandRepo;
  @Autowired private ProductRepo repo;

  @Mock private DistributedLockRegistry redisLockRegistry;

  private ProductService service;
  private ReentrantLock reentrantLock = new ReentrantLock();

  @Before
  public void setUp() {
    Mockito.when(redisLockRegistry.obtain(Mockito.anyString())).thenReturn(reentrantLock);
    service =
        new ProductServiceImpl(
            repo,
            new BrandServiceImpl(brandRepo, redisLockRegistry),
            new SellerServiceImpl(sellerRepo, redisLockRegistry),
            redisLockRegistry);

    Seller seller = sellerRepo.saveAndFlush(Seller.builder().name("seller").build());
    Brand brand = brandRepo.saveAndFlush(Brand.builder().name("brand").build());
    Category category = categoryRepo.saveAndFlush(Category.builder().name("category").build());

    repo.saveAndFlush(
        Product.builder()
            .name("product")
            .plId(123L)
            .sellerId(seller.getId())
            .brandId(brand.getId())
            .categories(Collections.singletonList(category))
            .build());
  }

  @After
  public void after() {
    repo.deleteAll();
    assertFalse(reentrantLock.isLocked());
  }

  @Test
  public void findAllByPLIDsInTest() {
    Page<Product> list =
        service.findAllByPLIDsIn(Collections.singletonList(123L), PageRequest.of(0, 10));

    assertNotNull(list);
    assertEquals(1, list.getContent().size());
    assertEquals(123L, list.getContent().get(0).getPlId().longValue());
  }

  @Test
  public void findByPlIDTest_exists() {
    Optional<Product> byPlID = service.findByPlID(123L);
    assertNotNull(byPlID);
    assertTrue(byPlID.isPresent());
    assertEquals(123L, byPlID.get().getPlId().longValue());
  }

  @Test
  public void findByPlIDTest_doesNotExist() {
    Optional<Product> byPlID = service.findByPlID(1L);
    assertNotNull(byPlID);
    assertFalse(byPlID.isPresent());
  }

  @Test
  public void findAllTest_category_PageRequest() {
    Page<Product> all = service.findAll(categoryRepo.findAll().get(0), PageRequest.of(0, 10));
    assertNotNull(all);
    assertEquals(1, all.getContent().size());
  }

  @Test
  public void findAllTest_brand_PageRequest() {
    Page<Product> all = service.findAll(brandRepo.findAll().get(0), PageRequest.of(0, 10));
    assertNotNull(all);
    assertEquals(1, all.getContent().size());
  }

  @Test
  public void findAllTest_PageRequest() {
    Page<Product> all = service.findAll(PageRequest.of(0, 10));
    assertNotNull(all);
    assertEquals(1, all.getContent().size());
  }

  @Test
  public void findOneTest_exists() {
    Optional<Product> one = service.findOne(repo.findAll().get(0).getId());
    assertNotNull(one);
    assertTrue(one.isPresent());
    assertEquals("product", one.get().getName());
  }

  @Test
  public void findOneTest_doesNotExists() {
    Optional<Product> one = service.findOne(repo.findAll().get(0).getId() + 1);
    assertNotNull(one);
    assertFalse(one.isPresent());
  }

  @Test
  public void saveTest() {
    Product product =
        service.save(Product.builder().name("product").plId(1L).brandId(1L).sellerId(1L).build());

    assertNotNull(product);
    assertEquals("product", product.getName());
  }

  @Test
  public void findAll_bySeller() {
    Seller seller = sellerRepo.findAll().get(0);

    Iterable<Product> all = service.findAll(seller, PageRequest.of(0, 10));

    assertNotNull(all);
    assertTrue(all.iterator().hasNext());
    assertNotNull(all.iterator().next().getSellerId());
  }
}
