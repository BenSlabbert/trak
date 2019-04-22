package io.github.benslabbert.trak.entity.jpa;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class TimestampEntity {

  @Column(nullable = false, name = "created")
  private Date created;

  @Column(nullable = false, name = "updated")
  private Date updated;

  @PrePersist
  protected void onCreate() {
    updated = created = new Date();
  }

  @PreUpdate
  protected void onUpdate() {
    updated = new Date();
  }
}
