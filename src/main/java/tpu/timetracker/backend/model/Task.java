package tpu.timetracker.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  @Column
  private String description;

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
}
