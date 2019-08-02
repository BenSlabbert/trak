package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.rabbitmq.event.promotion.PromotionEvent;
import io.github.benslabbert.trak.entity.rabbitmq.event.promotion.PromotionEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyDealPromotionsJob implements Runnable {

  private final RabbitTemplate rabbitTemplate;
  private final Queue promotionQueue;

  // todo this should run in the morning whe daily deals are live
  @Async
  @Override
  //    @Scheduled(cron = "0 0 0 0/1 * ?")
  @Scheduled(initialDelay = 0L, fixedDelay = Long.MAX_VALUE)
  public void run() {
    log.info("{}: Starting job", Promotion.DAILY_DEAL.getName());

    PromotionEvent event =
        PromotionEventFactory.createPromotionEvent(
            Promotion.DAILY_DEAL, UUID.randomUUID().toString());

    rabbitTemplate.convertAndSend(promotionQueue.getName(), event);

    log.debug("{}: Finished job", Promotion.DAILY_DEAL.getName());
  }
}
