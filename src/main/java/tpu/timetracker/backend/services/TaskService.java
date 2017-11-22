package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.TaskRepository;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;

  public Optional<Task> createTask(Project p, String name) {
    Objects.requireNonNull(p);
    Objects.requireNonNull(name);

    if (taskExistByName(p, name)) {
      throw new SecurityException(String.format("Task with that name: %s already exist!", name));
    }

    Task task = new Task(p);
    task.setName(name);
    return Optional.of(taskRepository.save(task));
  }

  public Optional<Task> createTask(Project p) {
    Objects.requireNonNull(p);

    Task task = new Task(p);
    return Optional.of(taskRepository.save(task));
  }

  public void deleteTask(Task t) {
    Objects.requireNonNull(t);
    taskRepository.delete(t);
  }

  public Collection<Task> getAllProjectTasks(Project p) {
    Objects.requireNonNull(p);

    return taskRepository.findAllByProject(p);
  }

  public Optional<Task> getTaskByProjectAndName(Project p, String name) {
    Objects.requireNonNull(p);
    Objects.requireNonNull(name);

    return taskRepository.findByProjectAndName(p, name);
  }

  public Optional<Task> taskExistById(Project p, String id) {
    Objects.requireNonNull(p);
    Objects.requireNonNull(id);

    return taskRepository.findByProjectAndId(p, id);
  }

  public boolean taskExistByName(Project p, String name) {
    Objects.requireNonNull(p);
    Objects.requireNonNull(name);

    return taskRepository.findByProjectAndName(p, name).isPresent();
  }
}
