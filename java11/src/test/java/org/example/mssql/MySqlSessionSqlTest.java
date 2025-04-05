package org.example.mssql;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MySqlSessionSqlTest {

    /**
     * capacity 정보 확인 1
     */
    @Test
    public void getMySqlSessionSqlTest01() {
        StringBuilder sql = new StringBuilder();
        sql.append(MySqlSessionSql.getMySqlSessionSql());
        Assert.assertEquals(sql.length(), sql.capacity());
    }

    /**
     * capacity 정보 확인 2
     */
    @Test
    public void getMySqlSessionSqlTest02() {
        StringBuilder sql = new StringBuilder(1000);
        sql.append(MySqlSessionSql.getMySqlSessionSql());
        if (sql.length() != 1000) {
            Assert.assertNotEquals(sql.length(), sql.capacity());
        }
    }
}