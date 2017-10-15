package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "TIMEENTRIES")
class TimeEntry extends AbstractEntity {

  private static final long serialVersionUID = 3527966648186016367L;

  private Long duration;

  private Date creationDate;

  public TimeEntry() {
  }
}
