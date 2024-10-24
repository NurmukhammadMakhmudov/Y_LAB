package com.example.y_lab.repositories;

import com.example.y_lab.services.ConnectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public class HabitRepositoryTest {

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
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByUser() {
    }
}
