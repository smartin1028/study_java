package org.example.mssql;


public class MySqlSessionSql {
    /**
     * sys.dm_exec_sessions는 세션 관련 상세 정보를 제공합니다.
     * @return sql
     */
    public static String getMySqlSample() {
        StringBuilder sql = new StringBuilder(100);
        sql.append("\nSELECT 1 CNT ");
        return sql.toString();
    }

    /**
     * sys.dm_exec_sessions는 세션 관련 상세 정보를 제공합니다.
     * @return sql
     */
    public static String getMySqlSessionSql() {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("\nSELECT session_id       /* 세션의 고유 식별자 */ ");
        sql.append("\n     , login_time       /* 사용자가 로그인한 시간 */ ");
        sql.append("\n     , host_name        /* 클라이언트 컴퓨터의 이름 */ ");
        sql.append("\n     , program_name     /* 연결에 사용된 애플리케이션 이름 */ ");
        sql.append("\n     , login_name       /* 로그인한 사용자의 이름 */ ");
        sql.append("\n     , status           /* 세션의 현재 상태 (running, sleeping 등) */ ");
        sql.append("\n     , cpu_time         /* 세션이 사용한 CPU 시간 (밀리초) */ ");
        sql.append("\n     , memory_usage     /* 세션이 사용하는 메모리 양 */ ");
        sql.append("\n  FROM sys.dm_exec_sessions ");
        sql.append("\n WHERE is_user_process = 1 ");
        return sql.toString();
    }

    /**
     * sys.dm_exec_connections는 네트워크 연결 정보를 확인할 때 사용합니다.
     * DmExecConnections
     * @return sql
     */
    public static String getDmExecConnections() {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("\n SELECT c.session_id");
        sql.append("\n      , c.connect_time");
        sql.append("\n      , c.protocol_type");
        sql.append("\n      , c.auth_scheme");
        sql.append("\n      , c.net_transport");
        sql.append("\n      , c.client_net_address");
        sql.append("\n   FROM sys.dm_exec_connections c");
        return sql.toString();
    }

    /**
     * 활성 쿼리 확인 쿼리는 현재 실행 중인 쿼리의 상세 정보를 보여줍니다.
     * @return sql
     */
    public static String getActiveQueriesSql() {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT r.session_id                                   \n");
        sql.append("     , r.status                                       \n");
        sql.append("     , r.command                                      \n");
        sql.append("     , r.cpu_time                                     \n");
        sql.append("     , r.total_elapsed_time                           \n");
        sql.append("     , t.text AS query_text                           \n");
        sql.append("     , s.login_name                                   \n");
        sql.append("     , s.host_name                                    \n");
        sql.append("     , s.program_name                                 \n");
        sql.append("  FROM sys.dm_exec_requests r                        \n");
        sql.append(" CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) t    \n");
        sql.append("  JOIN sys.dm_exec_sessions s                         \n");
        sql.append("   ON r.session_id = s.session_id                    \n");
        sql.append("WHERE r.session_id > 50                               \n");
        return sql.toString();
    }
}
