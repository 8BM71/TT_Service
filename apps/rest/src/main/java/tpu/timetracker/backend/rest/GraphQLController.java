package tpu.timetracker.backend.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class GraphQLController {

  private static final Logger log = LoggerFactory.getLogger(GraphQLController.class);

  @RequestMapping(value = "/gql", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
  public ResponseEntity execute() {
    return new ResponseEntity<>("hello!", HttpStatus.OK);
  }
}
