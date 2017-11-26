package tpu.timetracker.backend.graphql;

import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpu.timetracker.backend.services.UserService;
import tpu.timetracker.backend.services.WorkspaceService;

@Component
public class Fetchers {

  private static WorkspaceService workspaceService;

  private static UserService userService;

  @Autowired
  public Fetchers(WorkspaceService workspaceService, UserService userService) {
    Fetchers.workspaceService = workspaceService;
    Fetchers.userService = userService;
  }

  public static DataFetcher workspaceDataFetcher = environment -> {
    String wsId = environment.getArgument("id");
    String ownerId = environment.getArgument("ownerId");

    if (wsId == null)
      return workspaceService.getAllOwnerWorkspaces(ownerId);

    return workspaceService.getWorkspaceByOwnerIdAndId(ownerId, wsId).orElse(null);
  };

  public static DataFetcher userDataFetcher = environment -> {
    String id = environment.getArgument("id");
    return userService.getUserById(id).orElse(null);
  };

}
