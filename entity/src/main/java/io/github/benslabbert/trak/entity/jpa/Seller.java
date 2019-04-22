package io.github.benslabbert.trak.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(schema = Schema.TRAK, name = "seller")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
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
