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
@Document(indexName = "category_index", type = "category")
@NoArgsConstructor
@AllArgsConstructor
public class ESCategory implements ESSearchResult, Serializable {

  private static final long serialVersionUID = 8812861690856236186L;

  @Id private String id;

  private String name;

  @Score private Float score;
}
