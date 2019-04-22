package io.github.benslabbert.trak.engine.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BiggestSavingsJob implements Runnable {

    private final RabbitTemplate rabbitTemplate;
    private final Queue savingsQueue;

    @Override
    @Scheduled(initialDelay = 0L, fixedDelay = Long.MAX_VALUE)
    public void run() {

        log.info("Starting job");
        rabbitTemplate.convertAndSend(savingsQueue.getName(), UUID.randomUUID().toString());
        log.debug("Finished job");
    }
}
