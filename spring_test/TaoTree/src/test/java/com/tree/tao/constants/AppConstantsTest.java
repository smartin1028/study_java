package com.tree.tao.constants;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class AppConstantsTest {

    @Autowired AppConstants appConstants;
    @Autowired AppProperties appProperties;

    /**
     * 아래 파일이 자동으로 매핑되어 각 객체에 담긴것을 확인
     * src/main/resources/application-constants.yml
     */
    @Test
    public void t_AppConstantsTest_00() {
        log.info("######>> appConstants = {} " , appConstants);
        assertNotNull(appConstants);
        assertEquals("v1", appConstants.getApiVersion());
        assertEquals(5242880, appConstants.getMaxFileSize());
        assertArrayEquals(new String[]{"jpg", "png", "pdf"}, appConstants.getAllowedFileTypes());

        log.info("######>> appProperties = {} " , appProperties);
        assertEquals("v1", appProperties.getApiVersion());
        assertEquals(5242880, appProperties.getMaxFileSize());
        assertArrayEquals(new String[]{"jpg", "png", "pdf"}, appProperties.getAllowedFileTypes());

    }
}