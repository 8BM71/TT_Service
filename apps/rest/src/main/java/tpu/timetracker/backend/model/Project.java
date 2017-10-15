package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "PROJECTS")
class Project extends AbstractEntity {

  private static final long serialVersionUID = -319754945356826471L;

  private String name;

  private List<Task> tasks;

  public Project(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
