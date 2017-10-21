package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.TimeEntryRepository;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.TimeEntry;

import java.util.Objects;
import java.util.Optional;

@Service
public class TimeEntryService {

  @Autowired
  private TimeEntryRepository timeEntryRepository;

  public Optional<TimeEntry> createTimeEntry(Task t) {
    Objects.requireNonNull(t);

    if (t.getTimeEntry() != null) {
      return Optional.of(t.getTimeEntry());
    }

    TimeEntry timeEntry = new TimeEntry(t);
    return Optional.of(timeEntryRepository.save(timeEntry));
  }

  public void deleteTimeEntry(TimeEntry t) {
    Objects.requireNonNull(t);
    timeEntryRepository.delete(t);
  }

  public Optional<TimeEntry> getTimeEntryByTask(Task t) {
    Objects.requireNonNull(t);

    return timeEntryRepository.findByTask(t);
  }
}
