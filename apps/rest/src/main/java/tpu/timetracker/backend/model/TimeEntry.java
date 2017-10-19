package tpu.timetracker.backend.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TIMEENTRY")
public class TimeEntry extends AbstractEntity {

  private static final long serialVersionUID = 3527966648186016367L;

  @Column
  private Long duration;

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  private Date endDate;

  @OneToOne
  @JoinColumn(name = "TASK_ID")
  private Task task;

  public TimeEntry() {
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }
}
