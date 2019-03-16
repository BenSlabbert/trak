package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbit.event.PriceUpdateEvent;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;

@RunWith(SpringRunner.class)
public class PriceUpdateJobTest {

  @Mock private ProductService productService;
  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private SellerService sellerService;
  @Mock private Queue queue;

  @InjectMocks private PriceUpdateJob job;

  @Test
  public void runTest() {

    Seller seller = Seller.builder().id(1L).name("s1").build();
    PageImpl<Seller> sellers = new PageImpl<>(Collections.singletonList(seller));

    PageImpl<Product> products =
        new PageImpl<>(Collections.singletonList(Product.builder().id(1L).name("p1").build()));

    Mockito.when(sellerService.findAll(PageRequest.of(0, 10))).thenReturn(sellers);
    Mockito.when(productService.findAll(seller, PageRequest.of(0, 100))).thenReturn(products);
    Mockito.when(queue.getName()).thenReturn("queue");

    job.run();

    Mockito.verify(rabbitTemplate, atLeastOnce())
        .convertAndSend(eq("queue"), any(PriceUpdateEvent.class));
  }
}
