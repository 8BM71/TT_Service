package tpu.timetracker.backend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tpu.timetracker.backend.model.User;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByUsername(String username);
}
