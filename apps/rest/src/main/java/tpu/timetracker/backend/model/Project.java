package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PROJECT")
public class Project extends AbstractEntity {

  private static final long serialVersionUID = -319754945356826471L;

  @Size(min = 3)
  private String name;

  @ManyToOne
  @JoinColumn(name = "WORKSPACE_ID")
  private Workspace workspace;

  public Project(Workspace ws, String name) {
    this.workspace = ws;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Workspace getWorkspace() {
    return workspace;
  }
}
