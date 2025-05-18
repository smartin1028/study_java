package org.example.mssql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433";
//    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=YourDBName";
    private static final String USER = "SA";
    private static final String PASSWORD = "YourStrong@Passw0rd";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("데이터베이스 연결 성공!");
            return conn;
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패!");
            e.printStackTrace();
            return null;
        }
    }
}
