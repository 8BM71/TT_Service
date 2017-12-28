package tpu.timetracker.backend.graphql;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tpu.timetracker.backend.auth.GoogleTokenVerifierTemplate;
import tpu.timetracker.backend.model.*;
import tpu.timetracker.backend.services.*;

import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class MutationFetchers {
  private static WorkspaceService workspaceService;

  private static UserService userService;

  private static ProjectService projectService;

  private static TimeEntryService timeEntryService;

  private static TaskService taskService;

  private static GoogleTokenVerifierTemplate googleTokenVerifierTemplate;

  @Autowired
  public MutationFetchers(WorkspaceService workspaceService, UserService userService,
                       ProjectService projectService, TimeEntryService timeEntryService,
                       TaskService taskService, GoogleTokenVerifierTemplate googleTokenVerifierTemplate) {
    MutationFetchers.workspaceService = workspaceService;
    MutationFetchers.userService = userService;
    MutationFetchers.projectService = projectService;
    MutationFetchers.timeEntryService = timeEntryService;
    MutationFetchers.taskService = taskService;
    MutationFetchers.googleTokenVerifierTemplate = googleTokenVerifierTemplate;
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

    String projId = environment.getArgument("projId");

    Optional<Project> proj = projectService.getProjectById(projId);
    if ( ! proj.isPresent()) {
      throw new EntityExistsException("Project with that id: " + projId + " does not found");
    }

    if ( ! StringUtils.isEmpty(desc) && ! StringUtils.isEmpty(name)) {
      return returnDefault.with(taskService.createTask(proj.get(), name, desc));
    }

    if ( ! StringUtils.isEmpty(name)) {
      return returnDefault.with(taskService.createTask(proj.get(), name));
    }

    return returnDefault.with(taskService.createTask(proj.get()));
  };

  static DataFetcher updateTask = environment -> {
    Map<String, Object> m = environment.getArgument("task");
    String id = environment.getArgument("id");

    Optional<Task> task = taskService.getTaskById(id);
    if (! task.isPresent())
      throw new EntityExistsException("Task does not found");

    String desc = (String) m.get("description");
    String name = (String) m.get("name");
    String projId = (String) m.get("projectId");

    if (desc != null)
      task.get().setDescription(desc);
    if (name != null)
      task.get().setName(name);

    taskService.update(task.get());
    return true;
  };

  static DataFetcher removeTask = environment -> {
    String id = environment.getArgument("id");
    taskService.deleteTask(id);
    return true;
  };

  static DataFetcher startTask = environment -> {
    String taskid = environment.getArgument("taskId");

    Optional<Task> task = taskService.getTaskById(taskid);
    if (!task.isPresent()){
      return Optional.empty();
    }
    return timeEntryService.createTimeEntry(task.get());
  };

  static DataFetcher stopTimeEntry = environment -> {
      String timeEntryId = environment.getArgument("id");

      return timeEntryService.stopTimeEntry(timeEntryId);
  };

  static DataFetcher updateTimeEntry = environment -> {
    Map<String, Object> m = environment.getArgument("timeEntry");
    String id = environment.getArgument("id");

    Optional<TimeEntry> timeEntry = timeEntryService.getTimeEntryById(id);
    if (! timeEntry.isPresent())
      throw new EntityExistsException("Time entry does not found");

    Integer duration = (Integer) m.get("duration");
    String startDate = (String) m.get("startDate");
    String endDate = (String) m.get("endDate");

    if (duration != null)
      timeEntry.get().setDuration(duration);
    if (startDate != null)
      timeEntry.get().setStartDate(startDate);
    if (endDate != null)
      timeEntry.get().setEndDate(endDate);

    timeEntryService.update(timeEntry.get());
    return true;
  };

  static DataFetcher removeTimeEntry = environment -> {
    String id = environment.getArgument("id");
    timeEntryService.deleteTimeEntry(id);
    return true;
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

  static DataFetcher auth = environment -> {
    String token = environment.getArgument("token");

    try {
      GoogleIdToken googleIdToken = googleTokenVerifierTemplate.verify(token);
      if (Objects.isNull(googleIdToken)) {
        throw new SecurityException("Unauthorized");
      }

      String email = googleIdToken.getPayload().getEmail();
      Optional<User> userExist = userService.getUserByEmail(email);
      if (userExist.isPresent()) {
        return userExist;
      }

      String name = (String) googleIdToken.getPayload().get("given_name");
      String familyName = (String) googleIdToken.getPayload().get("family_name");
      Optional<User> newUser;
      if ( ! Objects.isNull(familyName) && ! Objects.isNull(name)) {
        newUser = userService.createUser(familyName, email, name);
      } else if ( ! Objects.isNull(familyName)) {
        newUser = userService.createUser(familyName, email);
      } else if ( ! Objects.isNull(name)) {
        newUser = userService.createUser(name, email);
      } else {
        newUser = userService.createUser(email, email);
      }

      return newUser;

    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }

    return Optional.empty();
  };
}
