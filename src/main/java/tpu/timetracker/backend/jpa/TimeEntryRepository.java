package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.TimeEntry;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;

@RepositoryRestResource(exported = false)
public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
  Collection<TimeEntry> findAllByWorkspace(Workspace w);
  Collection<TimeEntry> findAllByProject(Project p);
  Collection<TimeEntry> findAllByOwnerId(String ownerId);
}
