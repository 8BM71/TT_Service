package tpu.timetracker.backend.graphql;

import graphql.schema.idl.TypeRuntimeWiring;
import tpu.timetracker.backend.model.*;

import java.util.function.UnaryOperator;

class RuntimeWiringTypes {

  private static class CustomTypeRuntimeWiring {
    private CustomTypeRuntimeWiring() {
    }

    static TypeRuntimeWiring newTypeWiring(String typeName, UnaryOperator<TypeRuntimeWiring.Builder> builderFunction) {
      return builderFunction.apply(new TypeRuntimeWiring.Builder()
          .typeName(typeName)
          .dataFetcher("id", env -> env.<AbstractEntity>getSource().getId()))
          .dataFetcher("crdate", env -> env.<AbstractEntity>getSource().getCreationDate())
          .build();
    }
  }

  static TypeRuntimeWiring workspaceTypeWiring = CustomTypeRuntimeWiring.newTypeWiring(
      "Workspace", builder ->
          builder
              .dataFetcher("name", env -> env.<Workspace>getSource().getName())
              .dataFetcher("ownerId", env -> env.<Workspace>getSource().getOwnerId())
              .dataFetcher("project", QueryFetchers.projectDataFetcher)
              .dataFetcher("projects", QueryFetchers.projectsDataFetcher)
              .dataFetcher("timeEntries", QueryFetchers.timeEntriesDataFetcher)
              .dataFetcher("tasks", QueryFetchers.tasksDataFetcher));

  static TypeRuntimeWiring userTypeWiring = CustomTypeRuntimeWiring.newTypeWiring(
      "User", builder ->
          builder
              .dataFetcher("username", env -> env.<User>getSource().getUsername())
              .dataFetcher("name", env -> env.<User>getSource().getName())
              .dataFetcher("email", env -> env.<User>getSource().getEmail()));

  static TypeRuntimeWiring projectTypeWiring = CustomTypeRuntimeWiring.newTypeWiring(
      "Project", builder ->
          builder
              .dataFetcher("name", env -> env.<Project>getSource().getName())
              .dataFetcher("task", QueryFetchers.taskDataFetcher)
              .dataFetcher("tasks", QueryFetchers.tasksDataFetcher)
              .dataFetcher("timeEntries", QueryFetchers.timeEntriesDataFetcher));

  static TypeRuntimeWiring taskTypeWiring = CustomTypeRuntimeWiring.newTypeWiring(
      "Task", builder ->
          builder
              .dataFetcher("description", env -> env.<Task>getSource().getDescription())
              .dataFetcher("name", env -> env.<Task>getSource().getName())
              .dataFetcher("timeEntry", QueryFetchers.timeEntryDataFetcher)
              .dataFetcher("timeEntries", QueryFetchers.timeEntriesDataFetcher));

  static TypeRuntimeWiring timeEntryTypeWiring = CustomTypeRuntimeWiring.newTypeWiring(
      "TimeEntry", builder ->
          builder
              .dataFetcher("duration", env -> env.<TimeEntry>getSource().getDuration())
              .dataFetcher("endDate", env -> env.<TimeEntry>getSource().getEndDate())
              .dataFetcher("startDate", env -> env.<TimeEntry>getSource().getStartDate()));

  static TypeRuntimeWiring queryTypeWiring = TypeRuntimeWiring.newTypeWiring(
      "Query", builder ->
          builder
              .dataFetcher("workspace", QueryFetchers.workspaceDataFetcher)
              .dataFetcher("workspaces", QueryFetchers.workspaceDataFetcher)
              .dataFetcher("user", QueryFetchers.userDataFetcher));

  static TypeRuntimeWiring taskStates = TypeRuntimeWiring.newTypeWiring(
      "TaskState", builder ->
          builder.enumValues(name -> {
              switch (name) {
                case "CREATED": return 0;
                case "EXECUTED": return 1;
                case "COMPLETED": return 2;
              }
              return null; }));

  static TypeRuntimeWiring mutationTypeWiring = TypeRuntimeWiring.newTypeWiring(
      "Mutation", builder ->
          builder
              .dataFetcher("createUser", MutationFetchers.createUser)
              .dataFetcher("createWorkspace", MutationFetchers.createWorkspace)
              .dataFetcher("updateWorkspace", MutationFetchers.updateWorkspace)
              .dataFetcher("removeWorkspace", MutationFetchers.removeWorkspace)
              .dataFetcher("createProject", MutationFetchers.createProject)
              .dataFetcher("updateProject", MutationFetchers.updateProject)
              .dataFetcher("removeProject", MutationFetchers.removeProject)
              .dataFetcher("startTask", MutationFetchers.startTask)
              .dataFetcher("stopTimeEntry", MutationFetchers.stopTimeEntry)
              .dataFetcher("createTask", MutationFetchers.createTask));
}
