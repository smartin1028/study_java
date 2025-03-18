package com.tree.tao.service;

import com.tree.tao.dto.AiResponseHistoryDto;
import com.tree.tao.entity.AiResponseHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AiHistoryServiceTest {

    @Autowired private AiHistoryService aiHistoryService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() {
        List<AiResponseHistory> all = aiHistoryService.findAll();
        all.forEach(System.out::println);
    }

    @Test
    void addAiHistory() {
        /*

    private String prompt;      // 사용자 입력 프롬프트
    private String response;    // AI 응답 내용
    private String model;       // 사용된 AI 모델명
    private Integer tokenCount; // 사용된 토큰 수
    private String status;      // 응답 상태 (성공/실패)
    private Long processingTime; // 처리 소요 시간 (ms)
    @Column(length = 50)
    private String userId;      // 사용자 ID
         */

        AiResponseHistoryDto.createVo createVo = AiResponseHistoryDto.createVo.builder()
                .prompt("prompt")
                .response("response")
                .model("model")
                .tokenCount(1)
                .status("status")
                .processingTime(1L)
                .userId("testUser")
                .build();



        aiHistoryService.addAiHistory(createVo);

    }
}