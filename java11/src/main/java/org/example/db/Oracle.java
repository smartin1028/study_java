package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;


public class Oracle {

    public static Connection getConnection() {
        DBConnectionVo dbConnectionVo = new DBConnectionVo();
        DBType oracle = DBType.ORACLE;
        dbConnectionVo.setEtc1(":XE");
        // JDBC 연결 정보
        String jdbcUrl = DBConnectionVo.getJdbcUrl(dbConnectionVo, oracle);
        String username = dbConnectionVo.getUsername();
        String password = dbConnectionVo.getPassword();
        try {
            // 1. JDBC 드라이버 로드
            Class.forName(oracle.getClassName());
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
