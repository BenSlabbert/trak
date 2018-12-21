package com.trak.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(schema = Schema.TRAK, name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Product extends TimestampEntity implements Serializable {

  private static final long serialVersionUID = -3065356927583799612L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  @Column(name = "name")
  private String name;

  @Column(name = "sku")
  private String sku;

  @Column(name = "url")
  private String url;

  @Column(name = "api_endpoint")
  private String apiEndpoint;

  @OneToOne
  @JoinColumn(name = "seller_id", referencedColumnName = "id")
  private Seller seller;

  @OneToOne
  @JoinColumn(name = "brand_id", referencedColumnName = "id")
  private Brand brand;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "link_product_category",
      schema = Schema.TRAK,
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private List<Category> categories;
}
