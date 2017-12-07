package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {
  Optional<Task> findByProjectAndId(Project p, String id);

  Collection<Task> findAllByProject(Project p);
}
