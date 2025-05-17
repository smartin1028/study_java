package org.example.db;

import org.junit.Test;

import static org.junit.Assert.*;

public class DBConnectionVoTest {

    @Test
    public void getJdbcUrl() {

        DBConnectionVo dbConnectionVo = new DBConnectionVo();
        String jdbcUrl = DBConnectionVo.getJdbcUrl(dbConnectionVo, DBType.ORACLE);
        System.out.println("jdbcUrl = " + jdbcUrl);

        System.out.println("dbConnectionVo = " + dbConnectionVo);

    }
}