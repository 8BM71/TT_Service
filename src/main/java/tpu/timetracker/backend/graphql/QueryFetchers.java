package tpu.timetracker.backend.graphql;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpu.timetracker.backend.model.AbstractEntity;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.Task;
import tpu.timetracker.backend.model.Workspace;
import tpu.timetracker.backend.services.ProjectService;
import tpu.timetracker.backend.services.TaskService;
import tpu.timetracker.backend.services.TimeEntryService;
import tpu.timetracker.backend.services.UserService;
import tpu.timetracker.backend.services.WorkspaceService;

@Component
public class QueryFetchers {

  private static WorkspaceService workspaceService;

  private static UserService userService;

  private static ProjectService projectService;

  private static TimeEntryService timeEntryService;

  private static TaskService taskService;

  @Autowired
  public QueryFetchers(WorkspaceService workspaceService, UserService userService,
                       ProjectService projectService, TimeEntryService timeEntryService,
                       TaskService taskService) {
    QueryFetchers.workspaceService = workspaceService;
    QueryFetchers.userService = userService;
    QueryFetchers.projectService = projectService;
    QueryFetchers.timeEntryService = timeEntryService;
    QueryFetchers.taskService = taskService;
  }

  static DataFetcher workspaceDataFetcher = environment -> {
    String wsId = environment.getArgument("id");
    String ownerId = environment.getArgument("ownerId");

    if (wsId == null)
      return workspaceService.getAllOwnerWorkspaces(ownerId);

    return workspaceService.getWorkspaceByOwnerIdAndId(ownerId, wsId).orElse(null);
  };

  static DataFetcher userDataFetcher = environment -> {
    String id = environment.getArgument("id");
    return userService.getUserById(id).orElse(null);
  };

  static DataFetcher projectDataFetcher = environment -> {
    String id = environment.getArgument("id");
    return projectService.getProjectById(id).orElse(null);
  };

  static DataFetcher projectsDataFetcher = environment -> {
    Workspace w = environment.getSource();
    return projectService.getAllWorkspaceProjects(w);
  };

  static DataFetcher timeEntryDataFetcher = environment -> {
    String id = environment.getArgument("id");
    return timeEntryService.getTimeEntryById(id);
  };

  static DataFetcher timeEntriesDataFetcher = environment -> {
    AbstractEntity entity = environment.getSource();

    if (entity instanceof Workspace) {
      return timeEntryService.getAllByWorkspace(((Workspace) entity));
    }

    if (entity instanceof Project) {
      return timeEntryService.getAllByProject((Project) entity);
    }

    if (entity instanceof Task) {
      return timeEntryService.getAllByTask((Task) entity);
    }

    return null;
  };

  static DataFetcher taskDataFetcher = environment -> {
    String id = environment.getArgument("id");
    return taskService.getTaskById(id).orElse(null);
  };

  static DataFetcher tasksDataFetcher = environment -> {
    AbstractEntity entity = environment.getSource();

    if (entity instanceof Workspace) {
      return taskService.getAllWorkspaceTasks(((Workspace) entity));
    }

    if (entity instanceof Project) {
      return taskService.getAllProjectTasks((Project) entity);
    }
    return null;
  };
}
