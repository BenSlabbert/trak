package io.github.benslabbert.trak.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = Schema.TRAK, name = "brand")
public class Brand extends TimestampEntity implements Serializable {

  private static final long serialVersionUID = -916500778557574378L;

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
