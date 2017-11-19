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

@SpringBootTest(classes = BackendApplication.class)
@ContextConfiguration
class UserTest extends Specification {

  @Autowired
  UserService userService

  User user

  GraphQL graphQL

  def setup() {
    user = userService.createUser("username", "email").get()

    GraphQLSchema schema = RuntimeWiringBinder.generateSchema()
    graphQL = GraphQL.newGraphQL(schema).build()
  }

  def "get existing user"() {
    given:
    def query = """
      {
        user(id: "$user.id") {
          name
        }
      }
    """
    def re = graphQL.execute(query)

    expect:
    re.errors.size() == 0
    re.data.user.name == user.name
  }
}
