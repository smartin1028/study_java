package com.tree.tao.config;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class P6SpyFormatter implements MessageFormattingStrategy {

    @PostConstruct
    public void setP6SpyFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6SpyFormatter.class.getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(sql);
        return String.format("Connection ID: %d | Execution Time: %d ms | Category: %s | SQL: \n%s",
                connectionId, elapsed, category, sql);
    }

    private String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) return sql;
        // SQL 포맷팅 로직
        return sql.replaceAll("\\s+", " ")
                .replaceAll("\\( ", "(")
                .replaceAll(" \\)", ")")
                .replaceAll("\\s+(?i)select", "\nSELECT")
                .replaceAll("\\s+(?i)from", "\nFROM")
                .replaceAll("\\s+(?i)where", "\nWHERE")
                .replaceAll("\\s+(?i)and", "\n  AND")
                .replaceAll("\\s+(?i)or", "\n   OR")
                .replaceAll("\\s+(?i)order by", "\nORDER BY")
                .replaceAll("\\s+(?i)group by", "\nGROUP BY");
    }
}