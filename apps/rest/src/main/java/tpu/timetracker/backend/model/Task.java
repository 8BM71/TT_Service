package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TASKS")
class Task extends AbstractEntity {

  private static final long serialVersionUID = -4968820022482424460L;

  private TimeEntry timeEntry;

  private String description;

  private Project project;

  public Task(Project project) {
    this.project = project;
  }

  public TimeEntry getTimeEntry() {
    return timeEntry;
  }

  public void setTimeEntry(TimeEntry timeEntry) {
    this.timeEntry = timeEntry;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }
}
