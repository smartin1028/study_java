package com.tree.tao.dto;

import com.tree.tao.entity.AiResponseHistory;
import lombok.*;

public class AiResponseHistoryDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createVo {
        private String prompt;      // 사용자 입력 프롬프트
        private String response;    // AI 응답 내용
        private String model;       // 사용된 AI 모델명
        private Integer tokenCount; // 사용된 토큰 수
        private String status;      // 응답 상태 (성공/실패)
        private Long processingTime; // 처리 소요 시간 (ms)
        private String userId;      // 사용자 ID
    }

    /**
     * 등록할 데이터 convert
     * AiResponseHistoryDto.createVo to AiResponseHistory
     *
     * @param createVo 연결받은 데이터 정보
     * @return 생성할 데이터 엔티티
     */
    public static AiResponseHistory convert(AiResponseHistoryDto.createVo createVo) {

        return AiResponseHistory.builder()
                .prompt(createVo.getPrompt())
                .response(createVo.getResponse())
                .model(createVo.getModel())
                .tokenCount(createVo.getTokenCount())
                .status(createVo.getStatus())
                .processingTime(createVo.getProcessingTime())
                .userId(createVo.getUserId())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchVo {
        private Long id;
        private String prompt;      // 사용자 입력 프롬프트
        private String response;    // AI 응답 내용
        private String model;       // 사용된 AI 모델명
        private Integer tokenCount; // 사용된 토큰 수
        private String status;      // 응답 상태 (성공/실패)
        private Long processingTime; // 처리 소요 시간 (ms)
        private String userId;      // 사용자 ID
    }

}
