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
@Table(schema = Schema.TRAK, name = "best_savings")
public class BestSaving extends TimestampEntity implements Serializable {

    private static final long serialVersionUID = -5995699764260408266L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "saving", unique = true)
    private Float saving;

    @Column(name = "product_id", unique = true)
    private Long productId;

    @OneToOne
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false)
    private Product product;
}
