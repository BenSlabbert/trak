package io.github.benslabbert.trak.engine.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.CrawlerService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.event.crawler.CrawlerEvent;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CrawlerJobTest {

  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private CrawlerService crawlerService;
  @Mock private SellerService sellerService;
  @Mock private Queue queue;

  @InjectMocks private CrawlerJob job;

  @Test
  public void runTest() {

    Seller seller = Seller.builder().id(1L).name("s1").build();
    Crawler crawler = Crawler.builder().id(1L).lastId(0L).seller(seller).build();

    PageImpl<Seller> page = new PageImpl<>(Collections.singletonList(seller));

    Mockito.when(sellerService.findAll(PageRequest.of(0, 10))).thenReturn(page);
    Mockito.when(crawlerService.findBySeller(seller)).thenReturn(Optional.of(crawler));
    Mockito.when(queue.getName()).thenReturn("queue");

    job.run();

    Mockito.verify(rabbitTemplate, atLeastOnce())
        .convertAndSend(eq("queue"), any(CrawlerEvent.class));

    Mockito.verify(crawlerService, atLeastOnce()).save(any(Crawler.class));
  }
}
