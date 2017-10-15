package tpu.timetracker.backend.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USERS")
public class User extends AbstractEntity {

  private static final long serialVersionUID = 6922279983192374791L;

  @NotNull
  @Size(min = 3, max = 64)
  private String username;

  @Size(min = 3, max = 64)
  private String name;

  private String email;

  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}