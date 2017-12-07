package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
  Optional<Workspace> findByOwnerIdAndName(String ownerId, String name);

  Optional<Workspace> findByOwnerIdAndId(String ownerId, String id);

  Collection<Workspace> findAllByOwnerId(String ownerId);
}
