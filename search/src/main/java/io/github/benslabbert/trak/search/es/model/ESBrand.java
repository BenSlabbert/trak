package io.github.benslabbert.trak.search.es.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Score;

@Data
@Builder
@Document(indexName = "brand_index", type = "doc")
@NoArgsConstructor
@AllArgsConstructor
public class ESBrand implements ESSearchResult, Serializable {

  private static final long serialVersionUID = 8812861690856236186L;

  @Id private String id;

  private String name;

  @Score private Float score;
}
