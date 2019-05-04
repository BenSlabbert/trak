package io.github.benslabbert.trak.search.es.service;

import io.github.benslabbert.trak.search.es.model.ESBrand;
import io.github.benslabbert.trak.search.es.repo.ESBrandRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class ESBrandServiceImplIT {

  @Mock private ESBrandRepo repo;

  @InjectMocks private ESBrandServiceImpl service;

  @Before
  public void init() {
    assertNotNull(service);
  }

  @Test
  public void findBrandByNameLikeTest_noSpaces() {

    Mockito.when(repo.findAllByNameContaining("brand", PageRequest.of(0, 10)))
        .thenReturn(
            new PageImpl<>(Collections.singletonList(ESBrand.builder().name("brand").build())));

    Page<ESBrand> brand = service.findBrandByNameLike("brand", PageRequest.of(0, 10));
    assertNotNull(brand);
    assertEquals(1, brand.getContent().size());
    assertEquals("brand", brand.getContent().get(0).getName());
  }

  @Test
  public void findBrandByNameLikeTest_spaces() {

    Mockito.when(repo.findAllByNameContaining("brand1", PageRequest.of(0, 10)))
        .thenReturn(
            new PageImpl<>(Collections.singletonList(ESBrand.builder().name("brand1").build())));

    Page<ESBrand> brand = service.findBrandByNameLike("brand1 brand2", PageRequest.of(0, 10));
    assertNotNull(brand);
    assertEquals(1, brand.getContent().size());
    assertEquals("brand1", brand.getContent().get(0).getName());
  }
}
