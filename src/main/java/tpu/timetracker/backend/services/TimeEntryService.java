package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.TimeEntryRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.TimeEntry;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class TimeEntryService {

  @Autowired
  private TimeEntryRepository timeEntryRepository;

  @Autowired
  private TaskService taskService;

  public Optional<TimeEntry> createTimeEntry(Task t) {
    Objects.requireNonNull(t);

    if ( ! taskService.taskExist(t.getId())){
      throw new SecurityException(String.format("Task with that id: %s does not exist!", t.getId()));
    }

    TimeEntry timeEntry = new TimeEntry(t);
    return Optional.of(timeEntryRepository.save(timeEntry));
  }

  public void deleteTimeEntry(TimeEntry t) {
    Objects.requireNonNull(t);
    timeEntryRepository.delete(t);
  }

  public void deleteTimeEntry(String id) {
    Objects.requireNonNull(id);
    timeEntryRepository.delete(id);
  }

  public Collection<TimeEntry> getAllByWorkspace(Workspace w) {
    Objects.requireNonNull(w);

    return timeEntryRepository.findAllByWorkspace(w);
  }

  public Collection<TimeEntry> getAllByProject(Project p) {
    Objects.requireNonNull(p);

    return timeEntryRepository.findAllByProject(p);
  }

  public Collection<TimeEntry> getAllByOwnerId(String ownerId) {
    Objects.requireNonNull(ownerId);

    return timeEntryRepository.findAllByOwnerId(ownerId);
  }

  public Collection<TimeEntry> getAllByTask(Task t) {
    Objects.requireNonNull(t);

    return timeEntryRepository.findAllByTask(t);
  }

  public Optional<TimeEntry> getTimeEntryById(String id) {
    Objects.requireNonNull(id);

    return timeEntryRepository.findTimeEntryById(id);
  }
}
