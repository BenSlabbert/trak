package io.github.benslabbert.trak.entity.jpa;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

  @Column(name = "pl_id")
  private Long plId;

  @Column(name = "seller_id")
  private Long sellerId;

  @Column(name = "brand_id")
  private Long brandId;

  @OneToMany
  @JoinTable(
      name = "link_product_category",
      schema = Schema.TRAK,
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Category> categories;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "link_product_product_image",
      schema = Schema.TRAK,
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "product_image_id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<ProductImage> images;

  @Transient private Brand brand;

  @Transient private Seller seller;
}
