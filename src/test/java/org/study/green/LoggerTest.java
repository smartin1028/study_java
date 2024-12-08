package org.study.green;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void t_LoggerTest_true_00() {
    	
    	// eclipse에서 test resources는 생성이 안되게 막혀있음

        // 다양한 로그 레벨 사용
        logger.trace("TRACE 레벨 메시지");
        logger.debug("DEBUG 레벨 메시지");
        logger.info("INFO 레벨 메시지");
        logger.warn("WARN 레벨 메시지");
        logger.error("ERROR 레벨 메시지");

        // 파라미터화된 로깅
        String name = "SLF4J";
        int value = 42;
        logger.info("파라미터 예제: name={}, value={}", name, value);

        // 예외 로깅
//        try {
//            throw new RuntimeException("테스트 예외");
//        } catch (Exception e) {
//            logger.error("에러 발생", e);
//        }
    }
}
