package tpu.timetracker.backend.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@EntityListeners({AbstractEntity.AbstractEntityListener.class})
class AbstractEntity implements Serializable {

  private static final long serialVersionUID = -6229400610384798507L;

  private static final Logger log = LoggerFactory.getLogger(AbstractEntity.class);

  @Id
  @Column (length = 32)
  private String id;

  String uid() {
    if (id == null) {
      id = UUID.randomUUID().toString();
      log.debug("+ generated new id: {}, object: {}", id, toString());
    }

    return id;
  }

  public static class AbstractEntityListener {
    @PrePersist
    public void onPrePersist(AbstractEntity abstractEntity) {
      abstractEntity.uid();
    }
  }
}
