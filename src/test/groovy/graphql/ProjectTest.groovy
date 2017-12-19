package graphql

import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tpu.timetracker.backend.BackendApplication
import tpu.timetracker.backend.graphql.RuntimeWiringBinder
import tpu.timetracker.backend.model.Project
import tpu.timetracker.backend.model.User
import tpu.timetracker.backend.services.ProjectService
import tpu.timetracker.backend.services.UserService
import tpu.timetracker.backend.services.WorkspaceService

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class ProjectTest extends Specification{

    @Autowired
    ProjectService projectService

    @Autowired
    UserService userService

    @Autowired
    WorkspaceService workspaceService

    Project project

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

    def "get project by id"() {
        given:
        def ws = workspaceService.createWorkspace(user.getId(), "myWorkspace").get()
        def proj = projectService.createProject(ws, "myProject").get()
        def query = """
            {
                workspace(id: \"${ws.id}\", ownerId: \"${user.id}\") {
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
        re.data.workspace.project.id == proj.id
        re.data.workspace.project.name == proj.name

    }

    def "get all workspace projects"() {
        
    }

}
