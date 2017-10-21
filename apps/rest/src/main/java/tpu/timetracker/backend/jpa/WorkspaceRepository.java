package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
  Optional<Workspace> findByOwnerIdAndName(String ownerId, String name);
  Collection<Workspace> findAllByOwnerId(String ownerId);
}
