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
      .type(RuntimeWiringTypes.workspaceTypeWiring)
      .type(RuntimeWiringTypes.userTypeWiring)
      .type(RuntimeWiringTypes.projectTypeWiring)
      .type(RuntimeWiringTypes.taskTypeWiring)
      .type(RuntimeWiringTypes.timeEntryTypeWiring)
      .type(RuntimeWiringTypes.mutationTypeWiring)
      .build();

  public static GraphQLSchema generateSchema() {
    SchemaParser schemaParser = new SchemaParser();

    InputStream isQuery = RuntimeWiring.class.getResourceAsStream("/schema-query.graphqls");
    InputStream isMutation = RuntimeWiring.class.getResourceAsStream("/schema-mutation.graphqls");

    TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
    typeRegistry.merge(schemaParser.parse(new InputStreamReader(isQuery)));
    typeRegistry.merge(schemaParser.parse(new InputStreamReader(isMutation)));

    return new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
  }
}
