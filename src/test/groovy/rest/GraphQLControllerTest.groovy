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
import tpu.timetracker.backend.model.TaskState
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.model.Workspace
import tpu.timetracker.backend.rest.GraphQLController
import tpu.timetracker.backend.services.ProjectService
import tpu.timetracker.backend.services.TaskService
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

  @Autowired
  TaskService taskService

  User user

  def setup() {
    if (userService.userExist("email")) {
      user = userService.getUserByEmail("email").get()
    } else {
      user = userService.createUser("username", "email").get()
    }
  }

  def "create user"() {
    given:
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
      query: """
            mutation M (\$usr: UserInput!) {
              createUser(user: \$usr)
            }
          """,
      variables: {
        usr (
          username: "HONORminFieldLength-username",
          name: "HONORminFieldLength-name",
          email: "HONORminFieldLength-email"
        )
      }
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    content.errors == null
    content.data.createUser != null
  }

  def "create workspace"() {
    when:
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """
              mutation M (\$ws: WorkspaceInput!) {
                createWorkspace(workspace: \$ws)
              }
              """,
        variables: {
          ws (
              name: "someName",
              ownerId: user.id,
              description: "Somebody help me! They forced me to write tests!"
          )
        }
    )

    then:
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)
    def goVerify = { id ->
      workspaceService.getWorkspaceById(id).get()
    }

    expect:
    content.errors == null
    response.status == HttpStatus.OK.value()
    response.contentType == mediaType as String
    content.data.createWorkspace != null
    goVerify(content.data.createWorkspace as String).name == "someName"
    goVerify(content.data.createWorkspace as String).ownerId == user.id
    goVerify(content.data.createWorkspace as String).description == "Somebody help me! They forced me to write tests!"
  }

  def "update workspace"() {
    when:
    Workspace w = workspaceService.createWorkspace(user.id, "oldWorkspace").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$id: String!, \$ws: WorkspaceInput!) {
          updateWorkspace(id: \$id, workspace: \$ws)
        }""",
        variables: {
          id (w.id)
          ws (
              name: "updatedName",
              ownerId: user.id,
              description: "newDesk"
          )
        }
    )

    then:
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)
    def goVerify = { id ->
      workspaceService.getWorkspaceById(id).get()
    }

    expect:
    content.errors == null
    content.data.updateWorkspace
    goVerify(w.id).name == "updatedName"
    goVerify(w.id).description == "newDesk"
  }

  def "remove workspace"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "removeWorkspace").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$id: String!) {
          removeWorkspace(id: \$id)
        }""",
        variables: {
          id (w.id)
          ws (
              name: "updatedName",
              ownerId: user.id,
              description: "newDesk"
          )
        }
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    content.errors == null
    workspaceService.getWorkspaceById(w.id) == Optional.empty()
  }

  def "create project"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "mySecondWsByMutation").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$id: String!, \$p: ProjectInput!) {
          createProject(wsId: \$id, project: \$p)
        }""",
        variables: {
          id (w.id)
          p (name: "projName", color: 1234556)
        }
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)
    def goVerify = { id ->
      projectService.getProjectById(id).get()
    }

    expect:
    response.status == HttpStatus.OK.value()
    content.errors == null
    goVerify(content.data.createProject as String).name == "projName"
    projectService.getProjectByWorkspaceAndName(w, "projName").get().id
        .equals(content.data.createProject)

    projectService.getProjectByWorkspaceAndName(w, "projName").get().color
        .equals(1234556)
  }

  def "update project"() {
    when:
    Workspace w = workspaceService.createWorkspace(user.id, "updateProject").get()
    Project p = projectService.createProject(w, "updateProject", 100500).get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$projId: String!, \$project: ProjectInput!) {
          updateProject(projId: \$projId, project: \$project)
        }""",
        variables: {
          projId (p.id)
          project (
              name: "updatedName",
              color: 999999
          )
        }
    )

    then:
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)
    def goVerify = { id ->
      projectService.getProjectById(id).get()
    }

    expect:
    content.errors == null
    content.data.updateProject
    goVerify(p.id).name == "updatedName"
    goVerify(p.id).color == 999999
  }

  def "remove project"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "removeWorkspace").get()
    Project p = projectService.createProject(w, "removeProj").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$id: String!) {
          removeProject(id: \$id)
        }""",
        variables: {
          id (p.id)
        }
    )
    def response = mockMvc.perform(post("/graphql")
        .contentType(mediaType)
        .content(jsonBuilder.toString()))
        .andReturn()
        .response
    def content = new JsonSlurper().parseText(response.contentAsString)

    expect:
    content.errors == null
    projectService.getProjectById(p.id) == Optional.empty()
  }

  def "create task"() {
    given:
    Workspace w = workspaceService.createWorkspace(user.id, "myThirdWsByMutation").get()
    Project p = projectService.createProject(w, "MyBeautifulProject").get()
    def jsonBuilder = new JsonBuilder()
    jsonBuilder (
        query: """ mutation M (\$projId: String!, \$t: TaskInput!) {
          createTask(projId: \$projId, task: \$t)
        }""",
        variables: {
          projId (p.id)
          t (
              description: "my first task desc ever",
              name: "task may be created without any name",
              state: TaskState.CREATED
          )
        }
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
    taskService.getTaskById(content.data.createTask).isPresent()
    taskService.getTaskById(content.data.createTask).get().taskState == TaskState.CREATED
  }
}