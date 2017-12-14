package tpu.timetracker.backend.graphql;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tpu.timetracker.backend.model.AbstractEntity;
import tpu.timetracker.backend.model.Project;
import tpu.timetracker.backend.model.TaskState;
import tpu.timetracker.backend.model.Workspace;
import tpu.timetracker.backend.services.ProjectService;
import tpu.timetracker.backend.services.TaskService;
import tpu.timetracker.backend.services.TimeEntryService;
import tpu.timetracker.backend.services.UserService;
import tpu.timetracker.backend.services.WorkspaceService;

import javax.persistence.EntityExistsException;
import java.util.Map;
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

  @FunctionalInterface
  interface CustomDataFetcher<T> {
    T with(Optional<? extends AbstractEntity> op);
  }

  private static CustomDataFetcher<Object> returnDefault = (Optional<? extends AbstractEntity> op) -> {
    if (op.isPresent()) {
      return op.get().getId();
    } else {
      return Optional.empty();
    }
  };

  static DataFetcher createUser = environment -> {
    Map<String, Object> user = environment.getArgument("user");

    String username = (String) user.get("username");
    String name = (String) user.get("name");
    String email = (String) user.get("email");

    if (StringUtils.isEmpty(name)) {
      return returnDefault.with(userService.createUser(username, email));
    }

    return returnDefault.with(userService.createUser(username, email, name));
  };

  static DataFetcher createWorkspace = environment -> {
    Map<String, Object> m = environment.getArgument("workspace");
    String name = (String) m.get("name");
    String description = (String) m.get("description");
    String ownerId = (String) m.get("ownerId");

    if ( ! StringUtils.isEmpty(description)) {
      return returnDefault.with(workspaceService.createWorkspace(ownerId, name, description));
    }

    return returnDefault.with(workspaceService.createWorkspace(ownerId, name));
  };

  static DataFetcher updateWorkspace = environment -> {
    Map<String, Object> m = environment.getArgument("workspace");
    String targetId = environment.getArgument("id");

    String name = (String) m.get("name");
    String description = (String) m.get("description");
    String ownerId = (String) m.get("ownerId");

    Optional<Workspace> opw = workspaceService.getWorkspaceById(targetId);
    if ( ! opw.isPresent()) {
      throw new EntityExistsException("Workspace does not found");
    }

    opw.get().setName(name);
    if ( ! StringUtils.isEmpty(description)) {
      opw.get().setDescription(description);
    }

    workspaceService.update(opw.get());
    return true;
  };

  static DataFetcher removeWorkspace = environment -> {
    String id = environment.getArgument("id");
    workspaceService.deleteWorkspace(id);
    return true;
  };

  static DataFetcher createProject = environment -> {
    Map<String, Object> m = environment.getArgument("project");
    String projName = (String) m.get("name");
    String wsId = environment.getArgument("wsId");
    Integer color = (Integer) m.get("color");

    Optional<Workspace> ws = workspaceService.getWorkspaceById(wsId);
    if ( ! ws.isPresent()) {
      throw new EntityExistsException("Workspace with that id: " + wsId + " does not found");
    }

    return returnDefault.with(projectService.createProject(ws.get(), projName, color));
  };

  static DataFetcher updateProject = environment -> {
    Map<String, Object> m = environment.getArgument("project");
    String projId = environment.getArgument("projId");

    String name = (String) m.get("name");
    Integer color = (Integer) m.get("color");

    Optional<Project> opp = projectService.getProjectById(projId);
    if ( ! opp.isPresent()) {
      throw new EntityExistsException("Project does not found");
    }

    opp.get().setName(name);
    if (color != null) {
      opp.get().setColor(color);
    }

    projectService.update(opp.get());
    return true;
  };

  static DataFetcher removeProject = environment -> {
    String id = environment.getArgument("id");
    projectService.deleteProject(id);
    return true;
  };

  static DataFetcher createTask = environment -> {
    Map<String, Object> m = environment.getArgument("task");
    String desc = (String) m.get("description");
    String name = (String) m.get("name");
    Integer stateIndex = (Integer) m.get("state");
    TaskState state = TaskState.values()[stateIndex];

    String projId = environment.getArgument("projId");

    Optional<Project> proj = projectService.getProjectById(projId);
    if ( ! proj.isPresent()) {
      throw new EntityExistsException("Project with that id: " + projId + " does not found");
    }

    if ( ! StringUtils.isEmpty(desc) && ! StringUtils.isEmpty(name) && state != null) {
      return returnDefault.with(taskService.createTask(proj.get(), name, desc, state));
    }

    if ( ! StringUtils.isEmpty(desc) && ! StringUtils.isEmpty(name)) {
      return returnDefault.with(taskService.createTask(proj.get(), name, desc));
    }

    if ( ! StringUtils.isEmpty(name)) {
      return returnDefault.with(taskService.createTask(proj.get(), name));
    }

    if (state != null) {
      return returnDefault.with(taskService.createTask(proj.get(), state));
    }

    return returnDefault.with(taskService.createTask(proj.get()));
  };

  static DataFetcher stopTask = environment -> {
      String timeEntryId = environment.getArgument("timeEntryId");

      return timeEntryService.stopTimeEntry(timeEntryId);
  };

  static DataFetcher deleteProject = environment -> {
    String projId = environment.getArgument("projId");
    projectService.deleteProject(projId);

    Optional<Project> proj = projectService.getProjectById(projId);
    return  ! proj.isPresent();
  };

  static DataFetcher deleteWorkspace = environment -> {
    String wsId = environment.getArgument("wsId");
    workspaceService.deleteWorkspace(wsId);

    Optional<Workspace> ws = workspaceService.getWorkspaceById(wsId);
    return ! ws.isPresent();
  };
}
