package com.example.y_lab.services;

import org.hibernate.annotations.processing.SQL;

import java.sql.*;

public class ConnectionService {
    private final static String URL = "jdbc:postgresql://localhost:5432/y_lab?currentSchema=ylab_schema";
    private final static String USER_NAME = "y_lab";
    private final static String PASSWORD = "password";
    private Connection connection;

//    public static void main(String[] args) {
//        try{
//            Connection connection = getConnection();
//            Statement statement = connection.createStatement();
//            ResultSet resultSet =  statement.executeQuery("select count(*) from information_schema.tables");
//            while (resultSet.next())
//                System.out.println(resultSet.getInt("count"));
//        }
//        catch (SQLException e)
//        {
//            System.out.println(e.getMessage());
//        }
//
//    }

    public Connection getConnection() throws SQLException
    {
        this.connection = DriverManager.getConnection(URL,USER_NAME,PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

}
