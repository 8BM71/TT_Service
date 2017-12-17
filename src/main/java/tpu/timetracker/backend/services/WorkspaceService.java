package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.WorkspaceRepository;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class WorkspaceService {

  @Autowired
  private WorkspaceRepository workspaceRepository;

  @Autowired
  private UserService userService;

  public Optional<Workspace> createWorkspace(String ownerId, String name) {
    return createWorkspace(ownerId, name, null);
  }

  public Optional<Workspace> createWorkspace(String ownerId, String name, String description) {
    Objects.requireNonNull(ownerId);
    Objects.requireNonNull(name);

    if ( ! userService.getUserById(ownerId).isPresent()) {
      throw new SecurityException(String.format("User with that id: %s does not exist!", ownerId));
    }

    if (workspaceExist(ownerId, name)) {
      throw new SecurityException(String.format("Workspace with that name: %s already exist!", name));
    }

    Workspace ws = new Workspace(name, ownerId, description);
    return Optional.of(workspaceRepository.save(ws));
  }

  public void deleteWorkspace(Workspace ws) {
    Objects.requireNonNull(ws);
    workspaceRepository.delete(ws);
  }

  public void deleteWorkspace(String wsId) {
    Objects.requireNonNull(wsId);
    workspaceRepository.delete(wsId);
  }

  public Collection<Workspace> getAllOwnerWorkspaces(String ownerId) {
    Objects.requireNonNull(ownerId);
    return workspaceRepository.findAllByOwnerId(ownerId);
  }

  public Optional<Workspace> getWorkspaceByOwnerIdAndName(String ownerId, String name) {
    Objects.requireNonNull(ownerId);
    Objects.requireNonNull(name);

    return workspaceRepository.findByOwnerIdAndName(ownerId, name);
  }

  public Optional<Workspace> getWorkspaceById(String id) {
    Objects.requireNonNull(id);

    return Optional.ofNullable(workspaceRepository.findOne(id));
  }

  public Optional<Workspace> getWorkspaceByOwnerIdAndId(String ownerId, String id) {
    Objects.requireNonNull(ownerId);
    Objects.requireNonNull(id);

    return workspaceRepository.findByOwnerIdAndId(ownerId, id);
  }

  public boolean workspaceExist(String ownerId, String name) {
    Objects.requireNonNull(ownerId);
    Objects.requireNonNull(name);

    return workspaceRepository.findByOwnerIdAndName(ownerId, name).isPresent();
  }
}
