package org.tao.db;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLServerJdbcTemplateTest {
    @Test
    public void sqlServerJdbcTemplateTest00() {
        // TLS 1.2 활성화
        System.setProperty("javax.net.ssl.protocols", "TLSv1.2");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("http.protocols", "TLSv1.2");
        System.setProperty("com.microsoft.sqlserver.jdbc.SSL_PROTOCOL", "TLSv1.2");

        // DataSource 설정
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(DBType.SQL_SERVER.getClassName());
        dataSource.setUrl("jdbc:sqlserver://myhome.minipc.com:11433;"
                + "databaseName=master;encrypt=true;trustServerCertificate=true;");

        dataSource.setUsername("SA");
        dataSource.setPassword("YourStrong@Passw0rd");

        // JdbcTemplate 생성
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            // 임시 테이블 생성
//            jdbcTemplate.execute("CREATE TABLE #temp_test (id INT, name NVARCHAR(50))");

            // 데이터 삽입
//            jdbcTemplate.update("INSERT INTO #temp_test VALUES (?, ?)", 1, "Test Data");

            // 데이터 조회
            RowMapper<Map<String, Object>> rowMapper = new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                    System.out.println("cnt: " + resultSet.getInt("cnt"));
                    Map<String, Object> map = new HashMap<>();
                    map.put("cnt", resultSet.getInt("cnt"));
                    return map;
                }
            };
            List<Map<String, Object>> dataList = jdbcTemplate.query("SELECT 1 cnt ", rowMapper);
            for (Map<String, Object> stringObjectMap : dataList) {
                System.out.println("stringObjectMap = " + stringObjectMap);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
