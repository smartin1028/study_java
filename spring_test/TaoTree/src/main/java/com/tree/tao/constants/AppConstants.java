package com.tree.tao.constants;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * spring @value 테스트
 */
@Component
@Data
@ToString
public class AppConstants {
    @Value("${app.constants.max-file-size}")
    private long maxFileSize;

    @Value("${app.constants.allowed-file-types}")
    private String[] allowedFileTypes;

    @Value("${app.constants.api-version}")
    private String apiVersion;

    @Value("${spring.datasource.username}")
    String username;
}