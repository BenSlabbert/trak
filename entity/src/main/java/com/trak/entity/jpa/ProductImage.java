package com.trak.entity.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

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
