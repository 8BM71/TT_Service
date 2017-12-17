package tpu.timetracker.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpu.timetracker.backend.jpa.UserRepository;
import tpu.timetracker.backend.model.User;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public Optional<User> createUser(String username, String email) {
    return createUser(username, email, username);
  }

  public Optional<User> createUser(String username, String email, String name) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(email);
    Objects.requireNonNull(name);

    if (userExist(email)) {
      throw new SecurityException(String.format("User with email: %s already exist", email));
    }

    User user = new User(username, email, name);
    return Optional.of(userRepository.save(user));
  }

  public void deleteUser(User user) {
    Objects.requireNonNull(user);
    userRepository.delete(user);
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Optional<User> getUserById(String id) {
    return Optional.ofNullable(userRepository.findOne(id));
  }

  public boolean userExist(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

}
