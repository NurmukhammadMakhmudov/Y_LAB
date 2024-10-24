package com.example.y_lab.services;

import org.hibernate.annotations.processing.SQL;

import java.sql.*;

public class ConnectionService {
    private String url = "jdbc:postgresql://localhost:5432/y_lab?currentSchema=ylab_schema";
    private String username = "y_lab";
    private String password = "password";
    private final Connection connection;

    public ConnectionService(String url, String username, String password)  {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connection = DriverManager.getConnection(url,username,password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionService()  {
        try {
            this.connection = DriverManager.getConnection(url,username,password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException
    {
        return connection;
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

}
