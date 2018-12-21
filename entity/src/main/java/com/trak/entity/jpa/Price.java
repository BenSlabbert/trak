package com.trak.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = Schema.TRAK, name = "price")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Price implements Serializable {

  private static final long serialVersionUID = 8930973394610102320L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "current_price")
  private long currentPrice;

  @Column(name = "listed_price")
  private long listedPrice;

  @Column(name = "product_id")
  private long productId;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, name = "created")
  private Date created;

  @PrePersist
  protected void onCreate() {
    created = new Date();
  }
}
