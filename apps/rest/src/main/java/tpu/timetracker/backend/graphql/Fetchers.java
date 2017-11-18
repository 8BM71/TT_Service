package tpu.timetracker.backend.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpu.timetracker.backend.model.Workspace;
import tpu.timetracker.backend.services.WorkspaceService;

import java.util.Objects;
import java.util.Optional;

@Component
public class Fetchers {

  private static WorkspaceService workspaceService;

  @Autowired
  public Fetchers(WorkspaceService workspaceService) {
    Fetchers.workspaceService = workspaceService;
  }

  public static DataFetcher workspaceDataFetcher = environment -> {
    String wsId = environment.getArgument("id");
    String ownerId = environment.getArgument("ownerId");
    Optional<Workspace> ws = workspaceService.getWorkspaceByOwnerIdAndId(ownerId, wsId);
    return ws.orElse(null);
  };

  public static TypeRuntimeWiring.Builder workspaceFetcherBuilder(TypeRuntimeWiring.Builder builder) {
    Objects.requireNonNull(builder);
    return builder.dataFetcher("id", env -> env.<Workspace>getSource().getId())
        .dataFetcher("name", env -> env.<Workspace>getSource().getName())
        .dataFetcher("ownerId", env -> env.<Workspace>getSource().getOwnerId())
        .dataFetcher("crdate", env -> env.<Workspace>getSource().getCreationDate());
  }

}
