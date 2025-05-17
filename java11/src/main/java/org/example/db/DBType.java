package org.example.db;

public enum DBType {
    // jdbc:sqlserver://localhost:1433;databaseName=TestDB
    MYSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%s;"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s"),

    ;
    private String className;
    private String jdbcUrlFormat;

    DBType(String className, String jdbcUrl) {
        this.className = className;
        this.jdbcUrlFormat = jdbcUrl;
    }

    public String getClassName() {
        return className;
    }

    public String getJdbcUrl() {
        return jdbcUrlFormat;
    }
}
