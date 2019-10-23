package io.github.benslabbert.trak.search.sonic;

import io.github.benslabbert.trak.search.config.SonicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class SonicBlockingObjectPool {

  private final BlockingQueue<SonicChannelPrototype> sonicChannelPrototypeQueue;
  private final SonicConfig sonicConfig;

  public SonicBlockingObjectPool(
      final ObjectFactory<SonicChannelPrototype> sonicObjectFactory,
      final SonicConfig sonicConfig) {

    List<SonicChannelPrototype> instances = new ArrayList<>(sonicConfig.getPoolSize());
    for (Integer i = 0; i < sonicConfig.getPoolSize(); i++) {
      instances.add(sonicObjectFactory.getObject());
    }

    sonicChannelPrototypeQueue =
        new ArrayBlockingQueue<>(sonicConfig.getPoolSize(), true, instances);
    this.sonicConfig = sonicConfig;
  }

  @Scheduled(initialDelay = 1000L, fixedDelay = (1000L * 10L))
  public void ping() {
    List<SonicChannelPrototype> prototypes = new ArrayList<>(sonicConfig.getPoolSize());
    int i = sonicChannelPrototypeQueue.drainTo(prototypes, sonicConfig.getPoolSize());
    log.info("Drained {} from the pool to reconnect", i);

    prototypes.forEach(SonicChannelPrototype::ping);
    prototypes.forEach(this::returnInstance);
  }

  /**
   * Queries Sonic using one of the sonic channels in the connection pool. If no connections are
   * available this method will block until one becomes available
   */
  void ingestAndConsolidate(String collection, String bucket, Iterable<KV> pairs) {
    SonicChannelPrototype take = null;

    try {
      take = sonicChannelPrototypeQueue.take();
      for (KV p : pairs) take.ingest(collection, bucket, p.getObject(), p.getText());
      take.consolidate();
    } catch (InterruptedException e) {
      log.warn("Thread interrupted, cannot query Sonic!", e);
      Thread.currentThread().interrupt();
    } catch (IOException e) {
      log.error("Failed to query sonic, or no results available: ", e);
    } finally {
      returnInstance(take);
    }
  }

  /**
   * Queries Sonic using one of the sonic channels in the connection pool. If no connections are
   * available this method will block until one becomes available
   */
  List<String> query(String collection, String bucket, String text) {
    SonicChannelPrototype take = null;

    try {
      take = sonicChannelPrototypeQueue.take();
      return take.query(collection, bucket, text);
    } catch (InterruptedException e) {
      log.warn("Thread interrupted, cannot query Sonic!", e);
      Thread.currentThread().interrupt();
      return Collections.emptyList();
    } catch (IOException e) {
      log.error("Failed to query sonic, or no results available: ", e);
      return Collections.emptyList();
    } finally {
      returnInstance(take);
    }
  }

  /**
   * Queries Sonic using one of the sonic channels in the connection pool. If no connections are
   * available this method will block until one becomes available
   */
  List<String> suggest(String collection, String bucket, String text) {
    SonicChannelPrototype take = null;

    try {
      take = sonicChannelPrototypeQueue.take();
      return take.suggest(collection, bucket, text);
    } catch (InterruptedException e) {
      log.warn("Thread interrupted, cannot query Sonic!", e);
      Thread.currentThread().interrupt();
      return Collections.emptyList();
    } catch (IOException e) {
      log.error("Failed to query sonic, or no results available: ", e);
      return Collections.emptyList();
    } finally {
      returnInstance(take);
    }
  }

  private void returnInstance(SonicChannelPrototype take) {
    if (take != null) {
      try {
        sonicChannelPrototypeQueue.put(take);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        returnInstance(take);
      }
    } else {
      log.warn(
          "Failed to take a sonic instance from the pool, an exception is likely to be thrown");
    }
  }
}
