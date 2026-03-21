package org.tao.db;

import org.junit.Test;

public class DBConnectionVoTest {

    @Test
    public void getJdbcUrl() {

        DBConnectionVo dbConnectionVo = new DBConnectionVo();
        String jdbcUrl = DBConnectionVo.getJdbcUrl(dbConnectionVo, DBType.ORACLE);
        System.out.println("jdbcUrl = " + jdbcUrl);

        System.out.println("dbConnectionVo = " + dbConnectionVo);

    }
}