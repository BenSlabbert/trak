package io.github.benslabbert.trak.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = Schema.TRAK, name = "product_image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductImage implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  @Column(name = "url")
  private String url;
}
