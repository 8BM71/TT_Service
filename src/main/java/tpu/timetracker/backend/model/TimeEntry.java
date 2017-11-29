package tpu.timetracker.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "TIMEENTRY")
final public class TimeEntry extends AbstractEntity {

  private static final long serialVersionUID = 3527966648186016367L;

  @Column
  private Long duration;

  @Column
  private String startDate;

  @Column
  private String endDate;

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

    endDate = String.valueOf(new Date().getTime());
    startDate = String.valueOf(new Date().getTime());
    duration = 0L;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
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
