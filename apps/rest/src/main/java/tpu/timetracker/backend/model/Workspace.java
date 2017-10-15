package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "WORSPACES")
public class Workspace {

  @Size(min = 3)
  private String name;

  @Transient
  @OneToMany
  private List<Project> projects;

  public Workspace(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
