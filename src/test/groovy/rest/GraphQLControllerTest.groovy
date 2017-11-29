package rest

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import tpu.timetracker.backend.BackendApplication
import tpu.timetracker.backend.model.Project
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.model.Workspace
import tpu.timetracker.backend.rest.GraphQLController
import tpu.timetracker.backend.services.ProjectService
import tpu.timetracker.backend.services.UserService
import tpu.timetracker.backend.services.WorkspaceService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class GraphQLControllerTest extends Specification {

  GraphQLController controller = new GraphQLController()

  MockMvc mockMvc = standaloneSetup(controller).build()

  MediaType mediaType = MediaType.valueOf("application/json;charset=UTF-8")

  @Autowired
  UserService userService

  @Autowired
  WorkspaceService workspaceService

  @Autowired
  ProjectService projectService

  User user

  def setup() {
    if (userService.userExist("email")) {
      user = userService.getUserByEmail("email").get()
    } else {
      user = userService.createUser("username", "email").get()
    }
  }

  def "create workspace"() {
    when:
    def jsonBuilder = new JsonBuilder()
    def root = jsonBuilder (
      query: """ mutation M {
        createWorkspace(name: "myFirstWsByMutation", ownerId: "${user.id}") {
          name
        }
      }""",
      vars: ""
    )

    then:
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    response.status == HttpStatus.OK.value()
    response.contentType == mediaType as String
    content.data.createWorkspace.name == "myFirstWsByMutation"
  }

  def "create project"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "mySecondWsByMutation").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M {
      createProject(wsId: "${w.id}", name: "projByMutation") {
        id
        name
      }
    }
    """
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    response.status == HttpStatus.OK.value()
    content.data.createProject.name == "projByMutation"
  }

  def "create task"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "myThirdWsByMutation").get()
    Project p = projectService.createProject(w, "MyBeautifulProject").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M {
      createTask(projId: "${p.id}") {
        id
        timeEntry {
          endDate
          startDate
          duration
        }
      }
    }
    """
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    response.status == HttpStatus.OK.value()
    content.errors == null
    content.data.createTask.timeEntry.duration == 0
  }
}
