package tpu.timetracker.backend.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  private Integer duration;

  @Column
  private String startDate;

  @Column
  private String endDate;

  @JoinColumn(name = "OWNER_ID")
  private String ownerId;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "TASK_ID")
  private Task task;

  @ManyToOne
  @JoinColumn(name = "WORKSPACE_ID")
  private Workspace workspace;

  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  @ManyToOne
  @JoinColumn(name = "TASK_ID")
  private Task task;

  protected TimeEntry() {}

  public TimeEntry(Task t) {
    this.task = t;
    this.project = t.getProject();
    this.workspace = this.project.getWorkspace();
    this.ownerId = this.workspace.getOwnerId();

      startDate = String.valueOf(new Date().getTime());
      endDate = "";
      duration = 0L;
  }

  public TimeEntry(Task t, String startDate) {
    this.task = t;
    this.project = t.getProject();
    this.workspace = this.project.getWorkspace();
    this.ownerId = this.workspace.getOwnerId();
    this.startDate = startDate;
  }

  public TimeEntry(Task t, String endDate, String startDate) {
    this.task = t;
    this.project = t.getProject();
    this.workspace = this.project.getWorkspace();
    this.ownerId = this.workspace.getOwnerId();
    this.endDate = endDate;
    this.startDate = startDate;
  }

  public TimeEntry(Task t, String endDate, String startDate, Integer duration) {
    this.task = t;
    this.project = t.getProject();
    this.workspace = this.project.getWorkspace();
    this.ownerId = this.workspace.getOwnerId();
    this.endDate = endDate;
    this.startDate = startDate;
    this.duration = duration;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
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

  public Task getTask() {
    return task;
}
}
