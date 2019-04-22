package io.github.benslabbert.trak.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(schema = Schema.TRAK, name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Category extends TimestampEntity implements Serializable {

  private static final long serialVersionUID = -6767873842257115238L;

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
