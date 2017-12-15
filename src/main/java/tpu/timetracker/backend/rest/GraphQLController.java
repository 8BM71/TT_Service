package tpu.timetracker.backend.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class GraphQLController {

  private static final Logger log = LoggerFactory.getLogger(GraphQLController.class);

  @Autowired
  private ObjectMapper jacksonObjectMapper;

  private TypeReference<HashMap<String, Object>> typeRefReadJsonString = new TypeReference<HashMap<String, Object>>() {};

  private Map<String, Object> getVariablesMapFromString(String variablesFromRequest) {
    try {
      return jacksonObjectMapper.readValue(variablesFromRequest, typeRefReadJsonString);
    } catch (IOException exception) {
      throw new GraphQLException("Cannot parse variables", exception);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getVariablesFromRequest(Map<String, Object> body) {
    Object variablesFromRequest = body.get("vars");

    if (variablesFromRequest == null) {
      return Collections.emptyMap();
    }

    if (variablesFromRequest instanceof String) {
      if (StringUtils.hasText((String) variablesFromRequest)) {
        return getVariablesMapFromString((String) variablesFromRequest);
      }
    } else if (variablesFromRequest instanceof Map) {
      return (Map<String, Object>) variablesFromRequest;
    } else {
      throw new GraphQLException("Variables must be a Map");
    }

    return Collections.emptyMap();
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
