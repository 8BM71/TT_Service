package tpu.timetracker.backend.rest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tpu.timetracker.backend.graphql.RuntimeWiringBinder;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class GraphQLController {

  private static final Logger log = LoggerFactory.getLogger(GraphQLController.class);

  @SuppressWarnings("unchecked")
  private Map<String, Object> getVariablesFromRequest(Map<String, Object> body) {
    Object varsFromBody = body.get("vars");
    if (StringUtils.isEmpty(varsFromBody)){
      return Collections.emptyMap();
    } else if (varsFromBody instanceof Map) {
      return (Map<String, Object>) varsFromBody;
    } else {
      throw new GraphQLException("Variables must be a Map");
    }
  }

  @RequestMapping(value = "/graphql", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
  public ResponseEntity execute(@RequestBody Map<String, Object> body) {
    GraphQLSchema schema = RuntimeWiringBinder.generateSchema();
    GraphQL graphQL = GraphQL.newGraphQL(schema).build();

    String query = (String) body.get("query");
    Map<String, Object> vars = getVariablesFromRequest(body);

    ExecutionResult executionResult = graphQL.execute(
        ExecutionInput.newExecutionInput()
            .query(query)
            .variables(vars)
            .build());

    if ( ! executionResult.getErrors().isEmpty()) {
      log.error("There were errors during execution request: {}, errors: {}", body, executionResult.getErrors());
    }

    return new ResponseEntity<>(executionResult.toSpecification(), HttpStatus.OK);
  }
}
