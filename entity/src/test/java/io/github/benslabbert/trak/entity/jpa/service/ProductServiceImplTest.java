package io.github.benslabbert.trak.entity.jpa.service;

import io.github.benslabbert.trak.entity.config.JPATestConfig;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.repo.ProductRepo;
import io.github.benslabbert.trak.entity.jpa.repo.SellerRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static io.github.benslabbert.trak.entity.config.Profiles.JPA_TEST_POFILE;
import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = JPA_TEST_POFILE)
@ContextConfiguration(classes = JPATestConfig.class)
public class ProductServiceImplTest {

  @Autowired private ProductRepo repo;
  @Autowired private SellerRepo sellerRepo;

  // todo test OptimisticLockException
  private ProductService service;

  @Before
  public void setUp() {

    service = new ProductServiceImpl(repo);

    Seller seller = sellerRepo.saveAndFlush(Seller.builder().name("seller").build());

    repo.saveAndFlush(Product.builder().name("product").seller(seller).build());
  }

  @Test
  public void saveTest() {

    Product product = service.save(null);

    assertNotNull(product);
    assertEquals("product", product.getName());
  }

  @Test
  public void findAll_bySeller() {

    Seller seller = sellerRepo.findAll().get(0);

    Iterable<Product> all = service.findAll(seller, PageRequest.of(0, 10));

    assertNotNull(all);

    assertTrue(all.iterator().hasNext());

    assertEquals("seller", all.iterator().next().getSeller().getName());
  }
}
