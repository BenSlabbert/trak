package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.CrawlerService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class CreateCrawlerJobTest {

  @MockBean private RabbitTemplate rabbitTemplate;
  @MockBean private CrawlerService crawlerService;
  @MockBean private SellerService sellerService;
  @MockBean private Queue queue;

  // todo validate mock beans are called correctly
  private CreateCrawlerJob job;

  @Before
  public void setUp() {
    job = new CreateCrawlerJob(rabbitTemplate, crawlerService, sellerService, queue);
  }

  @Test
  public void runTest() {

    Seller seller = Seller.builder().name("seller").build();
    given(sellerService.findAll()).willReturn(Collections.singletonList(seller));

    Crawler crawler = Crawler.builder().seller(seller).lastId(1L).build();
    given(crawlerService.findBySeller(seller)).willReturn(Optional.of(crawler));

    job.run();

    Long lastId = crawler.getLastId();

    assertEquals(2L, lastId.longValue());
  }
}
