package com.example.y_lab.services;

import java.sql.*;

public class ConnectionService {
    private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String USER_NAME = "postgres";
    private final static String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER_NAME,PASSWORD);
    }

    public static void main(String[] args) {
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet =  statement.executeQuery("select count(*) from information_schema.tables");
            while (resultSet.next())
                System.out.println(resultSet.getInt("count"));
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }
}
