package tpu.timetracker.backend.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TASK")
final public class Task extends AbstractEntity {

  private static final long serialVersionUID = -4968820022482424460L;

  @Size(min = 3)
  private String name;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private TaskState taskState;

  @Column
  private String description;

  public Task(Project project, String description, String name, TaskState state) {
    this.project = project;
    this.description = description;
    this.name = name;
    this.taskState = state;
  }

  public Task(Project project) {
    this.project = project;
  }

  protected Task() {}

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project getProject() {
    return project;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TaskState getTaskState() {
    return taskState;
  }

  public void setTaskState(TaskState taskState) {
    this.taskState = taskState;
  }
}
