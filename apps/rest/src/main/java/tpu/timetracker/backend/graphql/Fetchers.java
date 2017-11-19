package tpu.timetracker.backend.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpu.timetracker.backend.model.User;
import tpu.timetracker.backend.model.Workspace;
import tpu.timetracker.backend.services.UserService;
import tpu.timetracker.backend.services.WorkspaceService;

import java.util.Objects;

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

  public static TypeRuntimeWiring.Builder workspaceFetcherBuilder(TypeRuntimeWiring.Builder builder) {
    Objects.requireNonNull(builder);
    return builder.dataFetcher("id", env -> env.<Workspace>getSource().getId())
        .dataFetcher("name", env -> env.<Workspace>getSource().getName())
        .dataFetcher("ownerId", env -> env.<Workspace>getSource().getOwnerId())
        .dataFetcher("crdate", env -> env.<Workspace>getSource().getCreationDate());
  }

  public static TypeRuntimeWiring.Builder userFetcherBuilder(TypeRuntimeWiring.Builder builder) {
    Objects.requireNonNull(builder);
    return builder.dataFetcher("id", env -> env.<User>getSource().getId())
        .dataFetcher("crdate", env -> env.<User>getSource().getCreationDate())
        .dataFetcher("username", env -> env.<User>getSource().getUsername())
        .dataFetcher("name", env -> env.<User>getSource().getName())
        .dataFetcher("email", env -> env.<User>getSource().getEmail());
  }

}
