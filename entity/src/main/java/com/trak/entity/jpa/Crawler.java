package com.trak.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = Schema.TRAK, name = "seller_crawler")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Crawler extends TimestampEntity implements Serializable {

  private static final long serialVersionUID = -7852876755488043669L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  @OneToOne
  @JoinColumn(name = "seller_id", referencedColumnName = "id")
  private Seller seller;

  @Column(name = "last_id")
  private Long lastId;
}
