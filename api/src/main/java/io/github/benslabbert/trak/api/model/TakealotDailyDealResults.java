package io.github.benslabbert.trak.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TakealotDailyDealResults {

    @JsonProperty("facets")
    private List<TakealotDailyDealFacet> facets;

    @JsonProperty("productlines")
    private List<TakealotDailyDealProduct> productLines;
}
