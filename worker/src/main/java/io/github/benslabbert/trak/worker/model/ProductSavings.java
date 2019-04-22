package io.github.benslabbert.trak.worker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ProductSavings {
    private long productId;
    private float savings;
}
