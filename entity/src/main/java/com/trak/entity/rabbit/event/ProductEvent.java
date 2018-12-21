package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Seller;

public interface ProductEvent extends Event {

  Seller getSeller();

  long getProductId();
}
