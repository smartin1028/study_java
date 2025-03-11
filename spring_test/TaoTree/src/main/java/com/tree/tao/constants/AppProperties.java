package com.tree.tao.constants;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * spring 설정값 객체 자동 매핑
 */
@ConfigurationProperties(prefix = "app.constants")
@Component
@Data
@ToString
public class AppProperties {
    private long maxFileSize;
    private String[] allowedFileTypes;
    private String apiVersion;
}
