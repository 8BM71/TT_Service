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

import java.util.Optional;

@Component
public class MutationFetchers {
  private static WorkspaceService workspaceService;

  private static UserService userService;

  private static ProjectService projectService;

  private static TimeEntryService timeEntryService;

  private static TaskService taskService;

  @Autowired
  public MutationFetchers(WorkspaceService workspaceService, UserService userService,
                       ProjectService projectService, TimeEntryService timeEntryService,
                       TaskService taskService) {
    MutationFetchers.workspaceService = workspaceService;
    MutationFetchers.userService = userService;
    MutationFetchers.projectService = projectService;
    MutationFetchers.timeEntryService = timeEntryService;
    MutationFetchers.taskService = taskService;
  }

  static DataFetcher createWorkspace = environment -> {
    String ownerId = environment.getArgument("ownerId");
    String name = environment.getArgument("name");

    return workspaceService.createWorkspace(ownerId, name);
  };

  static DataFetcher createProject = environment -> {
    String wsId = environment.getArgument("wsId");
    String name = environment.getArgument("name");

    Optional<Workspace> ws = workspaceService.getWorkspaceById(wsId);
    if ( ! ws.isPresent()) {
      return Optional.empty();
    }

    return projectService.createProject(ws.get(), name);
  };

  static DataFetcher createTask = environment -> {
    String projId = environment.getArgument("projId");

    Optional<Project> proj = projectService.getProjectById(projId);
    if ( ! proj.isPresent()) {
      return Optional.empty();
    }

    return taskService.createTask(proj.get());
  };
}
