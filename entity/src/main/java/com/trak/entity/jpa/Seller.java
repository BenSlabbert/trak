package com.trak.entity.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = Schema.TRAK, name = "seller")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Seller extends TimestampEntity implements Serializable {

  private static final long serialVersionUID = -8706806958759859385L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  @Column(name = "name", unique = true)
  private String name;
}
