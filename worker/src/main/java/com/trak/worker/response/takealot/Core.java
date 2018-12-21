package com.trak.worker.response.takealot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Core {

  @JsonProperty(value = "brand")
  private String brand;

  @JsonProperty(value = "title")
  private String title;

  @JsonProperty(value = "star_rating")
  private Double rating;
}
