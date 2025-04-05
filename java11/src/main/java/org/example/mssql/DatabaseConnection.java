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

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String sql = MySqlSessionSql.getMySqlSample();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // ResultSetMetaData 객체 얻기
                ResultSetMetaData metaData = rs.getMetaData();

                // 컬럼 수 가져오기
                int columnCount = metaData.getColumnCount();

                List<String> columnNames = new ArrayList<>();

                // 각 컬럼의 정보 출력
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                    System.out.println("컬럼명: " + metaData.getColumnName(i));
                    System.out.println("컬럼 타입: " + metaData.getColumnTypeName(i));
                    System.out.println("컬럼 크기: " + metaData.getColumnDisplaySize(i));
                }

                while (rs.next()) {
                    int row = rs.getRow();
                    System.out.println("row = " + row);
                    for (String columnName : columnNames) {
                        System.out.println(columnName + ": " + rs.getObject(columnName));
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("args = " + args);


    }
}
