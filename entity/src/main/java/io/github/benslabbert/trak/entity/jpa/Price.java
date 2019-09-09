package io.github.benslabbert.trak.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
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

  public static boolean equals(Price price1, Price price2) {

    price1 = normalise(price1);
    price2 = normalise(price2);

    return price1.getCreated().getTime() == price2.getCreated().getTime()
        && price1.getCurrentPrice() == price2.getCurrentPrice()
        && price1.getListedPrice() == price2.getListedPrice();
  }

  private static Price normalise(Price price) {
    return Price.builder()
        .id(price.getId())
        .listedPrice(price.getListedPrice())
        .currentPrice(price.getCurrentPrice())
        .created(normaliseDate(price.getCreated()))
        .build();
  }

  private static Date normaliseDate(Date date) {

    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);

    return c.getTime();
  }

  @PrePersist
  protected void onCreate() {
    created = new Date();
  }
}
