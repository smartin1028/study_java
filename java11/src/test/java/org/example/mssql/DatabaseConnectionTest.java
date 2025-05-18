package org.example.mssql;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    @Test
    public void test_main() {

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

    }
}