package com.tree.tao.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TB_AI_RESPONSE_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiResponseHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prompt;      // 사용자 입력 프롬프트

    @Column(nullable = false, columnDefinition = "CLOB")
    private String response;    // AI 응답 내용

    @Column(length = 50)
    private String model;       // 사용된 AI 모델명

    @Column
    private Integer tokenCount; // 사용된 토큰 수

    @Column(length = 20)
    private String status;      // 응답 상태 (성공/실패)

    @Column
    private Long processingTime; // 처리 소요 시간 (ms)

    @Column(length = 50)
    private String userId;      // 사용자 ID

    @Builder
    public AiResponseHistory(String prompt, String response, String model,
                           Integer tokenCount, String status, Long processingTime, String userId) {
        this.prompt = prompt;
        this.response = response;
        this.model = model;
        this.tokenCount = tokenCount;
        this.status = status;
        this.processingTime = processingTime;
        this.userId = userId;
    }
}
