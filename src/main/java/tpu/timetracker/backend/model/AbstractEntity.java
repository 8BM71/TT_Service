package tpu.timetracker.backend.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AbstractEntity.AbstractEntityListener.class)
public class AbstractEntity implements Serializable {

  private static final long serialVersionUID = -6229400610384798507L;

  private static final Logger log = LoggerFactory.getLogger(AbstractEntity.class);

  @Id
  @Column (length = 36)
  private String id;

  @Column(name = "CRDATE")
  private LocalDate creationDate;

  @PrePersist
  void creationDate() {
    this.creationDate = LocalDate.now();
  }

  String uid() {
    if (id == null) {
      id = UUID.randomUUID().toString();
      log.debug("+ generated new id: {}, object: {}", id, toString());
    }

    return id;
  }

  public String getId() {
    return id;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public static class AbstractEntityListener {
    @PrePersist
    public void onPrePersist(AbstractEntity abstractEntity) {
      abstractEntity.uid();
    }
  }
}
