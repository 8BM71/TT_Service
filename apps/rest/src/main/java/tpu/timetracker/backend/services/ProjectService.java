package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.ProjectRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {

  @Autowired
  private ProjectRepository projectRepository;

  public Optional<Project> createProject(Workspace ws, String name) {
    Objects.requireNonNull(ws);
    Objects.requireNonNull(name);

    if (projectExist(ws, name)) {
      throw new SecurityException(String.format("Project with that name: %s already exist!", name));
    }

    Project project = new Project(ws, name);
    return Optional.of(projectRepository.save(project));
  }

  public void deleteProject(Project p) {
    Objects.requireNonNull(p);
    projectRepository.delete(p);
  }

  public Optional<Project> getProjectByWorkspaceAndName(Workspace ws, String name) {
    Objects.requireNonNull(ws);
    Objects.requireNonNull(name);

    return projectRepository.findByWorkspaceAndName(ws, name);
  }

  public Collection<Project> getAllWorkspaceProjects(Workspace ws) {
    Objects.requireNonNull(ws);
    return projectRepository.findAllByWorkspace(ws);
  }

  public boolean projectExist(Workspace ws, String name) {
    Objects.requireNonNull(ws);
    Objects.requireNonNull(name);

    return projectRepository.findByWorkspaceAndName(ws, name).isPresent();
  }
}
