package tpu.timetracker.backend.rest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tpu.timetracker.backend.graphql.RuntimeWiringBinder;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class GraphQLController {

  private static final Logger log = LoggerFactory.getLogger(GraphQLController.class);

  @RequestMapping(value = "/graphql", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
  public ResponseEntity execute(@RequestBody Map<String, Object> body) {
    GraphQLSchema schema = RuntimeWiringBinder.generateSchema();
    GraphQL graphQL = GraphQL.newGraphQL(schema).build();

    String query = (String) body.get("query");
    ExecutionInput executionInput = ExecutionInput.newExecutionInput()
        .query(query)
        .build();
    ExecutionResult executionResult = graphQL.execute(executionInput);

    if ( ! executionResult.getErrors().isEmpty()) {
      log.error("There were errors during execution request: {}, errors: {}", body, executionResult.getErrors());
    }

    return new ResponseEntity<>(executionResult.toSpecification(), HttpStatus.OK);
  }
}
