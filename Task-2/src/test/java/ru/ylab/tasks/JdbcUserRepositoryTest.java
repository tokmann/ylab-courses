package ru.ylab.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task2.constant.Role;
import ru.ylab.tasks.task2.model.User;
import ru.ylab.tasks.task2.repository.jdbc.JdbcUserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcUserRepositoryTest {

    private JdbcUserRepository repository;

    @BeforeEach
    void setUp() {
        TestDatabase.clearData();
        repository = new JdbcUserRepository(TestDatabase.getConnection());
    }

    @Test
    void save_shouldInsertNewUser() {
        User user = new User("testuser", "password", Role.USER);

        repository.save(user);

        assertThat(user.getId()).isNotNull();

        Optional<User> found = repository.findByLogin("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("testuser");
        assertThat(found.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void save_shouldUpdateExistingUser() {
        User user = new User("original", "pass1", Role.USER);
        repository.save(user);
        Long id = user.getId();

        user.setPassword("newpass");
        user.setRole(Role.ADMIN);
        repository.save(user);

        Optional<User> updated = repository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().getPassword()).isEqualTo("newpass");
        assertThat(updated.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User user = new User("test", "pass", Role.USER);
        repository.save(user);

        Optional<User> found = repository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("test");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByLogin_shouldReturnUserWhenExists() {
        User user = new User("johndoe", "password", Role.USER);
        repository.save(user);

        Optional<User> found = repository.findByLogin("johndoe");

        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("johndoe");
    }

    @Test
    void findByLogin_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = repository.findByLogin("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        repository.save(new User("user1", "pass1", Role.USER));
        repository.save(new User("user2", "pass2", Role.ADMIN));

        List<User> users = repository.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void existsByLogin_shouldReturnTrueWhenLoginExists() {
        repository.save(new User("existing", "pass", Role.USER));

        boolean exists = repository.existsByLogin("existing");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByLogin_shouldReturnFalseWhenLoginNotExists() {
        boolean exists = repository.existsByLogin("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User user = new User("todelete", "pass", Role.USER);
        repository.save(user);
        Long id = user.getId();

        boolean deleted = repository.deleteById(id);

        assertThat(deleted).isTrue();
        Optional<User> found = repository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldReturnFalseWhenUserNotExists() {
        boolean deleted = repository.deleteById(999L);

        assertThat(deleted).isFalse();
    }
}
