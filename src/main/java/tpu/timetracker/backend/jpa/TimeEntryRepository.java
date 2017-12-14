package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.TimeEntry;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
  Collection<TimeEntry> findAllByWorkspace(Workspace w);

  Collection<TimeEntry> findAllByProject(Project p);

  Collection<TimeEntry> findAllByOwnerId(String ownerId);

  Collection<TimeEntry> findAllByTask(Task t);

  Optional<TimeEntry> findTimeEntryById(String id);
}
