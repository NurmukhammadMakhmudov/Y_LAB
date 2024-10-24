package com.example.y_lab.repositories;

import com.example.y_lab.models.User;
import com.example.y_lab.services.ConnectionService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Testcontainers
public class UserRepositoryTest {
    private ConnectionService connectionService;
    private Connection connection;

    @Container
    public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    public void setUp() throws SQLException {
         connectionService = new ConnectionService(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
         connection = connectionService.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE users (" +
                    "    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "    email VARCHAR(255) NOT NULL UNIQUE, " +
                    "    password VARCHAR(255) NOT NULL," +
                    "    username VARCHAR(255) NOT NULL," +
                    "    is_blocked BOOLEAN NOT NULL" +
                    ");");
            stmt.execute("INSERT INTO users ( email, password, username, is_blocked) VALUES ( 'joe@gmail.com', " +
                    "'password', 'Joe', false)");
            stmt.execute("INSERT INTO users ( email, password, username, is_blocked) VALUES ( 'ann@gmail.com', " +
                    "'password1', 'ann', false)");
            stmt.execute("INSERT INTO users ( email, password, username, is_blocked) VALUES ( 'jack@gmail.com', " +
                    "'password2', 'Jack', true)");
            stmt.execute("CREATE TABLE habits (" +
                            "    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                            "    title VARCHAR(255) NOT NULL," +
                            "    description TEXT NOT NULL," +
                            "    frequency VARCHAR(255) NOT NULL," +
                            "    creation_date DATE NOT NULL," +
                            "    user_id BIGINT," +
                            "    CONSTRAINT fk_user_habits FOREIGN KEY (user_id)" +
                            "    REFERENCES users(id) ON DELETE CASCADE" +
                            ");"
                    );
            stmt.execute("INSERT INTO habits ( title, description, frequency, creation_date, user_id) VALUES ( 'to do', " +
                    "'to do', 'daily', '2024-10-10', 1)");
            stmt.execute("INSERT INTO habits ( title, description, frequency, creation_date, user_id) VALUES ( 'to do', " +
                    "'to do', 'daily', '2024-10-11', 2)");
            stmt.execute("INSERT INTO habits ( title, description, frequency, creation_date, user_id) VALUES ( 'to do', " +
                    "'to do', 'daily', '2024-10-12', 3)");

            stmt.execute("CREATE TABLE habit_completions (" +
                    "    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "    date DATE NOT NULL," +
                    "    completed BOOLEAN NOT NULL," +
                    "    habit_id BIGINT," +
                    "    CONSTRAINT fk_habit_completions_habit FOREIGN KEY (habit_id)" +
                    "    REFERENCES habits(id) ON DELETE CASCADE" +
                    ");"
            );
            stmt.execute("INSERT INTO habit_completions ( date, completed, habit_id) VALUES ( '2024-10-19', " +
                    "true, 1)");
            stmt.execute("INSERT INTO habit_completions ( date, completed, habit_id) VALUES ( '2024-10-20', " +
                    "true, 1)");
            stmt.execute("INSERT INTO habit_completions ( date, completed, habit_id) VALUES ( '2024-10-21', " +
                    "true, 2)");
        }
    }


    @AfterEach
    public void tearDown() throws Exception {
        connection.close();
    }


    @Test
    public void testFindUserById() {
        UserRepository userRepository = new UserRepository(connectionService);
        User user = userRepository.findById(1L);

        assertNotNull(user);
        assertEquals("Joe", user.getName());
    }

    @Test
    @SneakyThrows
    void save() {
        UserRepository userRepository = new UserRepository(connectionService);
        connectionService = new ConnectionService(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
        connection = connectionService.getConnection();
        User user = new User();
        user.setPassword("pass");
        user.setName("new User");
        user.setEmail("new@mail.org");
        user.setBlocked(false);
        userRepository.save(user);
        String sql = "select count(*) from users";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        int result = 0;
        if (resultSet.next())
            result = resultSet.getInt("count");
        assertEquals(4, result);
        connection.close();
    }


    @Test
    @SneakyThrows
    void update() {
        UserRepository userRepository = new UserRepository(connectionService);
        User user = new User();
        user.setId(3L);
        user.setPassword("pass");
        user.setName("new updated User");
        user.setEmail("newUpdated@mail.org");
        user.setBlocked(true);
        userRepository.update(user);
        connectionService = new ConnectionService(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
        connection = connectionService.getConnection();
        String sql = "select * from users where id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, user.getId());
        ResultSet resultSet  = preparedStatement.executeQuery();
        User updatedUser = new User();
        if (resultSet.next())
        {
            updatedUser.setId(resultSet.getLong("id"));
            updatedUser.setEmail(resultSet.getString("email"));
            updatedUser.setPassword(resultSet.getString("password"));
            updatedUser.setBlocked(resultSet.getBoolean("is_blocked"));
            updatedUser.setName(resultSet.getString("username"));
        }
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(user.isBlocked(), updatedUser.isBlocked());



    }


    @Test
    @SneakyThrows
    void findByEmail() {
        UserRepository userRepository = new UserRepository(connectionService);
        User user = userRepository.findByEmail("joe@gmail.com");
        connectionService = new ConnectionService(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
        connection = connectionService.getConnection();
        String sql = "select * from users where email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "joe@gmail.com");
        ResultSet resultSet  = preparedStatement.executeQuery();
        User updatedUser = new User();
        if (resultSet.next())
        {
            updatedUser.setId(resultSet.getLong("id"));
            updatedUser.setEmail(resultSet.getString("email"));
            updatedUser.setPassword(resultSet.getString("password"));
            updatedUser.setBlocked(resultSet.getBoolean("is_blocked"));
            updatedUser.setName(resultSet.getString("username"));
        }
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(user.isBlocked(), updatedUser.isBlocked());


    }

    @Test
    @SneakyThrows
    void findAll() {
        UserRepository userRepository = new UserRepository(connectionService);
        List<User> userList = userRepository.findAll();
        assertEquals(3, userList.size());
    }

    @Test
    @SneakyThrows
    void delete() {
        UserRepository userRepository = new UserRepository(connectionService);
        userRepository.delete(1L);
        connectionService = new ConnectionService(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
        connection = connectionService.getConnection();
        String sql = "select count(*) from users";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        int result = 0;
        if (resultSet.next())
            result = resultSet.getInt("count");
        assertEquals(2, result);
        connection.close();
    }
}
