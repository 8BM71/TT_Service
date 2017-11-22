package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface ProjectRepository extends JpaRepository<Project, String> {
  Optional<Project> findByWorkspaceAndName(Workspace ws, String name);
  Collection<Project> findAllByWorkspace(Workspace ws);
}
