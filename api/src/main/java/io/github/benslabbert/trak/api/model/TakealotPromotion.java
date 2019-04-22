package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TakealotPromotion implements Serializable {

    @JsonProperty("response")
    private List<TakealotPromotionsResponse> responseList;
}
