package graphql

import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tpu.timetracker.backend.BackendApplication
import tpu.timetracker.backend.graphql.RuntimeWiringBinder
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.services.UserService
import tpu.timetracker.backend.services.WorkspaceService

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class WorkspaceTest extends Specification {

  @Autowired
  WorkspaceService workspaceService

  @Autowired
  UserService userService

  User user

  GraphQL graphQL

  def setup() {
    user = userService.createUser("username", "email").get()

    GraphQLSchema schema = RuntimeWiringBinder.generateSchema()
    graphQL = GraphQL.newGraphQL(schema).build()
  }

  def "createWorkspace test"() {
    when:
    def ws = workspaceService.createWorkspace(user.getId(), "testWorkspace").get()

    then:
    def query = "{workspace(id: \"${ws.id}\", ownerId: \"${user.id}\") {name}}"
    ExecutionResult result = graphQL.execute(query)

    expect:
    result.errors.size() == 0
    result.data.workspace.name == ws.getName()
  }
}
