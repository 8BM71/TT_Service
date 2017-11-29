package tpu.timetracker.backend.graphql;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;

public class RuntimeWiringBinder {

  private static RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
      .type(RuntimeWiringTypes.queryTypeWiring)
      .type(RuntimeWiringTypes.mutationTypeWiring)
      .type(RuntimeWiringTypes.workspaceTypeWiring)
      .type(RuntimeWiringTypes.userTypeWiring)
      .type(RuntimeWiringTypes.projectTypeWiring)
      .type(RuntimeWiringTypes.taskTypeWiring)
      .type(RuntimeWiringTypes.timeEntryTypeWiring)
      .build();

  public static GraphQLSchema generateSchema() {
    SchemaParser schemaParser = new SchemaParser();

    InputStream is = RuntimeWiring.class.getResourceAsStream("/schema.graphqls");

    TypeDefinitionRegistry typeRegistry = schemaParser.parse(new InputStreamReader(is));
    return new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
  }
}
