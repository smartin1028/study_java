package org.example.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlServerTest {

    @Test
    public void t_SqlServerTest_true_00() {

        System.out.println("SqlServer DB에 연결중");
        try (Connection conn = SqlServer.getConnection(); Statement stmt = conn.createStatement()) {

            System.out.println("SqlServer DB에 연결 완료");
            try (// 데이터 조회
                 ResultSet rs = stmt.executeQuery("SELECT 'hello' str")) {
                while (rs.next()) {
                    System.out.println("str: " + rs.getString("str"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}