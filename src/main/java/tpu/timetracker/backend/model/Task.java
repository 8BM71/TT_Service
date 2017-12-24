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
  @JoinColumn(name = "WORKSPACE_ID")
  private Workspace workspace;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  @Column
  private String description;

  public Task(Project project, String description, String name) {
    this.project = project;
    this.workspace = this.project.getWorkspace();
    this.description = description;
    this.name = name;
  }

  public Task(Project project) {
    this.project = project;
    this.workspace = this.project.getWorkspace();
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

  public Workspace getWorkspace() { return workspace; }
}
