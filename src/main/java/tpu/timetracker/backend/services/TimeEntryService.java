package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.TimeEntryRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.TimeEntry;
import tpu.timetracker.backend.model.Workspace;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class TimeEntryService {

  @Autowired
  private TimeEntryRepository timeEntryRepository;

  @Autowired
  private TaskService taskService;

  public Optional<TimeEntry> createTimeEntry(Task t) {
    return createTimeEntry(t, null, null, null);
  }

  public Optional<TimeEntry> createTimeEntry(Task t, String startDate) {
    return createTimeEntry(t, null, startDate, null);
  }

  public Optional<TimeEntry> createTimeEntry(Task t, String endDate, String startDate) {
    return createTimeEntry(t, endDate, startDate, null);
  }

  public Optional<TimeEntry> createTimeEntry(Task t, String endDate, String startDate, Integer duration) {
    Objects.requireNonNull(t);

    if ( ! taskService.taskExist(t.getId())){
      throw new SecurityException(String.format("Task with that id: %s does not exist!", t.getId()));
    }

    TimeEntry timeEntry = new TimeEntry(t, endDate, startDate, duration);
    return Optional.of(timeEntryRepository.save(timeEntry));
  }

  public Optional<TimeEntry> stopTimeEntry(String id) {
    Objects.requireNonNull(id);

    Optional<TimeEntry> timeEntry = timeEntryRepository.findById(id);
    if (!timeEntry.isPresent()) {
      throw new SecurityException(String.format("Time entry with: %s not exist", id));
    }

    TimeEntry te = timeEntry.get();
    te.setEndDate(String.valueOf(new Date().getTime()));
    Long duration = Long.valueOf(te.getEndDate()) - Long.valueOf(te.getStartDate());
    te.setDuration(Math.toIntExact(duration));

    return Optional.of(timeEntryRepository.save(te));
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

  public boolean timeEntryExist(String id) {
    Objects.requireNonNull(id);

    return getTimeEntryById(id).isPresent();
  }
}
