package graphql

import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tpu.timetracker.backend.BackendApplication
import tpu.timetracker.backend.graphql.RuntimeWiringBinder
import tpu.timetracker.backend.model.Project
import tpu.timetracker.backend.model.Task
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.model.Workspace
import tpu.timetracker.backend.services.ProjectService
import tpu.timetracker.backend.services.TaskService
import tpu.timetracker.backend.services.UserService
import tpu.timetracker.backend.services.WorkspaceService

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class TaskTest extends Specification {

    @Autowired
    TaskService taskService
    Task task

    @Autowired
    ProjectService projectService
    Project project

    @Autowired
    WorkspaceService workspaceService
    Workspace workspace

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

        if(workspaceService.workspaceExist(user.id, "testW")) {
            workspace = workspaceService.getWorkspaceByOwnerIdAndName(user.id, "testW").get()
        } else {
            workspace = workspaceService.createWorkspace(user.id, "testW").get()
        }

        if(projectService.projectExist("testP")) {
            project = projectService.getProjectById("testP").get()
        } else {
            project = projectService.createProject(workspace, "testP").get()
        }

        //task = taskService.createTask(project)

        GraphQLSchema schema = RuntimeWiringBinder.generateSchema()
        graphQL = GraphQL.newGraphQL(schema).build()
    }

    def "create task"() {
        given:
        task = taskService.createTask(project).get()

        expect:
        task.id != ""
        task.timeEntry.id != ""
    }

}
