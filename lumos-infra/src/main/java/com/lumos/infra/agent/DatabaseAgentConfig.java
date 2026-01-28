package com.lumos.infra.agent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DatabaseAgentConfig {

    public record SqlRequest(String sql) {}
    public record SqlResponse(List<Map<String, Object>> results) {}

    @Bean
    public FunctionCallback databaseQueryTool(JdbcClient jdbcClient) {
        return FunctionCallbackWrapper.builder(new DatabaseQueryFunction(jdbcClient))
                .withName("databaseQueryTool")
                .withDescription("执行只读 SQL 查询。支持查询 ideas 和 audit_logs 表。输入应为 SELECT 语句。")
                .build();
    }

    private record DatabaseQueryFunction(JdbcClient jdbcClient) implements Function<SqlRequest, SqlResponse> {
        @Override
        public SqlResponse apply(SqlRequest request) {
            String sql = request.sql().trim();
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            log.info("Agent executing SQL: {}", sql);

            if (!sql.toUpperCase().startsWith("SELECT")) {
                return new SqlResponse(List.of(Map.of("error", "Only SELECT is allowed")));
            }

            try {
                return new SqlResponse(jdbcClient.sql(sql).query().listOfRows());
            } catch (Exception e) {
                return new SqlResponse(List.of(Map.of("error", e.getMessage())));
            }
        }
    }
}
