package io.github.benslabbert.trak.core.rabbitmq;

public class Queue {

  //  WORKER QUEUES
  public static final String CRAWLER_QUEUE = "crawler";
  public static final String PRODUCT_QUEUE = "product";
  public static final String PRICE_QUEUE = "price";
  public static final String SAVINGS_QUEUE = "savings";
  public static final String PROMOTIONS_QUEUE = "promotions";

  // SONIC QUEUES
  public static final String SONIC_INGEST_QUEUE = "sonic_ingest_queue";

  private Queue() {}
}
