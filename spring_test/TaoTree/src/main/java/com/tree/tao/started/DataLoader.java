package com.tree.tao.started;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("애플리케이션 시작이 완료되었습니다.");
        // 여기에 데이터 표시 로직 구현
    }
}
