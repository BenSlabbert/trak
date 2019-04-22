package io.github.benslabbert.trak.search.es.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Score;

@Data
@Builder
@Document(indexName = "product_index", type = "doc")
@NoArgsConstructor
@AllArgsConstructor
public class ESProduct implements ESSearchResult, Serializable {

  private static final long serialVersionUID = 8812861690856236186L;

  @Id private String id;

  private String name;

  @JsonProperty("pl_id")
  @Field(type = FieldType.Long)
  private Long plId;

  @Score private Float score;
}
