package kr.growth.eum.feign.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RandomSaltUtilTest {

    @Test
    void generateRandomSalt() {
        String s = RandomSaltUtil.generateRandomSalt();
        assertNotNull(s);
        log.info("Generated random salt: {}", s);
        log.info("length : {}", s.length());
        String s1 = RandomSaltUtil.generateRandomSalt(100);
        log.info("Generated random salt: {}", s1);
        log.info("length : {}", s1.length());

    }
}
