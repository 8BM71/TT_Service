package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.WorkspaceRepository;
import tpu.timetracker.backend.model.User;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class WorkspaceService {

  @Autowired
  private WorkspaceRepository workspaceRepository;

  public Optional<Workspace> createWorkspace(User owner, String name) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(name);

    if (workspaceExist(owner.getId(), name)) {
      throw new SecurityException(String.format("Workspace with that name: %s already exist!", name));
    }

    Workspace ws = new Workspace(name, owner.getId());
    return Optional.of(workspaceRepository.save(ws));
  }

  public void deleteWorkspace(Workspace ws) {
    Objects.requireNonNull(ws);
    workspaceRepository.delete(ws);
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

  public boolean workspaceExist(String ownerId, String name) {
    Objects.requireNonNull(ownerId);
    Objects.requireNonNull(name);

    return workspaceRepository.findByOwnerIdAndName(ownerId, name).isPresent();
  }
}
