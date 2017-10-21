package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task, String> {
  Optional<Task> findByProjectAndName(Project p, String name);
  Optional<Task> findByProjectAndId(Project p, String id);
  Collection<Task> findAllByProject(Project p);
}
