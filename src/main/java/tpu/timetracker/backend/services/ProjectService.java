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

  @Autowired
  private WorkspaceService workspaceService;

  public Optional<Project> createProject(Workspace ws, String name) {
    return  createProject(ws, name, null);
  }

  public Optional<Project> createProject(Workspace ws, String name, Integer color) {
    Objects.requireNonNull(ws);
    Objects.requireNonNull(name);

    if ( ! workspaceService.getWorkspaceById(ws.getId()).isPresent()){
      throw new SecurityException(String.format("Workspace with that id: %s does not exist!", ws.getId()));
    }

    if (projectExist(ws, name)) {
      throw new SecurityException(String.format("Project with that name: %s already exist!", name));
    }

    Project project = new Project(ws, name, color);
    return Optional.of(projectRepository.save(project));
  }

  public void deleteProject(Project p) {
    Objects.requireNonNull(p);
    projectRepository.delete(p);
  }

  public void deleteProject(String projId) {
    Objects.requireNonNull(projId);
    projectRepository.delete(projId);
  }

  public Optional<Project> getProjectById(String projId) {
    Objects.requireNonNull(projId);
    return Optional.ofNullable(projectRepository.findOne(projId));
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

  public boolean projectExist(String projId) {
    Objects.requireNonNull(projId);
    return getProjectById(projId).isPresent();
  }

  public boolean projectExist(Workspace ws, String name) {
    Objects.requireNonNull(ws);
    Objects.requireNonNull(name);

    return projectRepository.findByWorkspaceAndName(ws, name).isPresent();
  }
}
