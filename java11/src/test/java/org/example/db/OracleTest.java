package org.example.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class OracleTest {

    @Test
    public void t_OracleTest_true_00() {
        // 기본 properties info
        // -Ddb_ip=localhost -Ddb_port=1521 -Ddb_id=system -Ddb_password=password

        System.out.println("Oracle DB에 연결중");
        try (Connection conn = Oracle.getConnection(); Statement stmt = conn.createStatement()) {

            System.out.println("Oracle DB에 연결 완료");
            try (// 데이터 조회
                 ResultSet rs = stmt.executeQuery("SELECT 'hello' str FROM dual")) {
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