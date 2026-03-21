package org.example.db;

import org.junit.Test;
import org.tao.utils.db.DBConnectionVo;
import org.tao.utils.db.DBType;

public class DBConnectionVoTest {

    @Test
    public void getJdbcUrl() {

        DBConnectionVo dbConnectionVo = new DBConnectionVo();
        String jdbcUrl = DBConnectionVo.getJdbcUrl(dbConnectionVo, DBType.ORACLE);
        System.out.println("jdbcUrl = " + jdbcUrl);

        System.out.println("dbConnectionVo = " + dbConnectionVo);

    }
}