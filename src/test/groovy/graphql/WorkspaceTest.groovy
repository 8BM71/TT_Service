package graphql

import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tpu.timetracker.backend.BackendApplication
import tpu.timetracker.backend.graphql.RuntimeWiringBinder
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.services.ProjectService
import tpu.timetracker.backend.services.TaskService
import tpu.timetracker.backend.services.UserService
import tpu.timetracker.backend.services.WorkspaceService

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class WorkspaceTest extends Specification {

  @Autowired
  WorkspaceService workspaceService

  @Autowired
  ProjectService projectService

  @Autowired
  TaskService taskService

  @Autowired
  UserService userService

  User user

  GraphQL graphQL

  def setup() {
    if (userService.userExist("email")) {
      user = userService.getUserByEmail("email").get()
    } else {
      user = userService.createUser("username", "email").get()
    }

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

  def "get project from workspace"() {
    given:
    def ws = workspaceService.createWorkspace(user.getId(), "3testWorkspace").get()
    def proj = projectService.createProject(ws, "myProject").get()
    def query = """
      {
        workspace(id: \"${ws.id}\", ownerId: \"${user.id}\") {
          name
          project(id: \"${proj.id}\") {
            id
            name
          }
        }
      }
    """
    def re = graphQL.execute(query)

    expect:
    re.errors.size() == 0
    re.data.workspace.name == ws.name
    re.data.workspace.project.name == proj.name
    re.data.workspace.project.id == proj.id
  }

  def "get many projects from workspace"() {
    given:
    def ws = workspaceService.createWorkspace(user.getId(), "4testWorkspace").get()
    List projs = []
    10.times { i ->
      projs << projectService.createProject(ws, "proj$i").get()
    }
    def query = """
      {
        workspace(id: \"${ws.id}\", ownerId: \"${user.id}\") {
          projects {
            name
          }
        }
      }
    """
    def re = graphQL.execute(query)

    expect:
    re.errors.size() == 0
    10.times { i ->
      projs[i].name == re.data.workspace.projects[i].name
    }
  }

  def "get many tasks from workspace's project"() {
    given:
    def w = workspaceService.createWorkspace(user.getId(), "5testWorkspace").get()
    def p = projectService.createProject(w, "5myProject").get()
    def t = taskService.createTask(p).get()
    def query = """
      {
        workspace(id: \"${w.id}\", ownerId: \"${user.id}\") {
          project(id: \"${p.id}\") {
            task(id: \"${t.id}\") {
              id
            }
          }
        }
      }
    """
    def re = graphQL.execute(query)

    expect:
    re.errors.size() == 0
    re.data.workspace.project.task.id == t.id
  }

  def "workspace assertion test"() {
    when:
    def ws = workspaceService.createWorkspace(user.id, "thisIsMyWs")

    then:
    Assert.assertNotNull(ws)
  }
}
