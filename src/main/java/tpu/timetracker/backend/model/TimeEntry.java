package tpu.timetracker.backend.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TIMEENTRY")
final public class TimeEntry extends AbstractEntity {

  private static final long serialVersionUID = 3527966648186016367L;

  @Column
  private Long duration;

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  private Date endDate;

  @JoinColumn(name = "OWNER_ID")
  private String ownerId;

  @ManyToOne
  @JoinColumn(name = "WORKSPACE_ID")
  private Workspace workspace;

  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  protected TimeEntry() {}

  public TimeEntry(Task t) {
    this.project = t.getProject();
    this.workspace = this.project.getWorkspace();
    this.ownerId = this.workspace.getOwnerId();
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

  public String getOwnerId() {
    return ownerId;
  }

  public Workspace getWorkspace() {
    return workspace;
  }

  public Project getProject() {
    return project;
  }
}
