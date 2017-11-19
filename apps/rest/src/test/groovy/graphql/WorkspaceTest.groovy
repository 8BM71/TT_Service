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

  def "get existing workspace"() {
    when:
    def ws = workspaceService.createWorkspace(user.getId(), "testWorkspace").get()

    then:
    def query = "{workspace(id: \"${ws.id}\", ownerId: \"${user.id}\") {name id crdate ownerId}}"
    ExecutionResult result = graphQL.execute(query)
    def resultWs = result.data.workspace

    expect:
    result.errors.size() == 0
    resultWs.name == ws.name
    resultWs.id == ws.id
    resultWs.ownerId == user.id
    resultWs.crdate == ws.creationDate as String
  }

  def "get all workspaces by ownerId"() {
    when:
    def wsList = []
    5.times { it ->
      wsList << workspaceService.createWorkspace(user.id, "ws$it")
    }

    then:
    def query = """
      {
        workspaces(ownerId: \"${user.id}\") {name ownerId}
      }
    """
    ExecutionResult er = graphQL.execute(query)
    List r = er.data.workspaces

    expect:
    5.times { index ->
      r[index].name == wsList[index].get().name
    }

    r*.ownerId.unique()[0] == user.id
  }

  def "get all fields of ws"() {
    given:
    def ws = workspaceService.createWorkspace(user.id, "testWorkspace").get()
    def query = """ {
        workspace(id: "${ws.id}", ownerId: "${user.id}")
      }
    """
    def er = graphQL.execute(query)
    println er
  }
}
