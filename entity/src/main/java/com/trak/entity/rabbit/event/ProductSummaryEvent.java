package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Product;

public interface ProductSummaryEvent extends Event {

  Product getProduct();
}
