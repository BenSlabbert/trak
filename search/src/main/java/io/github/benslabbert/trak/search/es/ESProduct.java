package io.github.benslabbert.trak.search.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Data
@Builder
@Document(indexName = "product")
@NoArgsConstructor
@AllArgsConstructor
public class ESProduct implements Serializable {

  private static final long serialVersionUID = 8812861690856236186L;

  @Id private String id;

  private Long rdsId;

  private String name;
}
