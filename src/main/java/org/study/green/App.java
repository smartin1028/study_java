package org.study.green;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new App();
	}

	public App() {
        // 다양한 로그 레벨 사용
        logger.trace("TRACE 레벨 메시지");
        logger.debug("DEBUG 레벨 메시지");
        logger.info("INFO 레벨 메시지");
        logger.warn("WARN 레벨 메시지");
        logger.error("ERROR 레벨 메시지");
	}
}
