package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.TimeEntry;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
  Optional<TimeEntry> findByTask(Task t);
  Collection<TimeEntry> findAllByTask(Task p);
}
