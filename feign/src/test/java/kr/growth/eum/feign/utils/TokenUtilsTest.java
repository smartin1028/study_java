package kr.growth.eum.feign.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TokenUtilsTest {

    @Test
    void generateBearerToken() {
        String s = TokenUtils.generateBearerToken();
        Assertions.assertNotNull(s);
        log.info("Generated bearer token: {}", s);
        log.info("Generated size : {}", s.length());
    }

    @Test
    void testGenerateBearerToken() {
        String s = TokenUtils.generateBearerToken(10);
        Assertions.assertNotNull(s);
        log.info("Generated bearer token: {}", s);
        log.info("Generated size : {}", s.length());
    }
}
