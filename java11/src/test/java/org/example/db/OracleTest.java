package org.example.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class OracleTest {

    @Test
    public void t_OracleTest_true_00() {

        System.out.println("Oracle DB에 연결중");
        try (
                Connection conn = Oracle.getConnection();
                Statement stmt = conn.createStatement()) {
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