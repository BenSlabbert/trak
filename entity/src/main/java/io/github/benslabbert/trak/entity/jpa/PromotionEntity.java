package io.github.benslabbert.trak.entity.jpa;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = Schema.TRAK, name = "promotion")
public class PromotionEntity extends TimestampEntity implements Serializable {
  private static final long serialVersionUID = -15405149467467631L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  @Column(name = "name")
  private String name;

  @Column(name = "takealot_promotion_id")
  private Long takealotPromotionId;

  @OneToMany
  @JoinTable(
      name = "link_promotion_product",
      schema = Schema.TRAK,
      joinColumns = @JoinColumn(name = "promotion_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Product> products;
}
