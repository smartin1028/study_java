package com.tree.tao.dto;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class EventDTO {

    //
    @Schema(type = "string", pattern = "yyyy-MM-dd HH:mm:ss", example = "2025-03-16 15:30:00")
    private String eventDateTime;

    // LocalDateTime으로 변환이 필요할 때 사용
    public String convertToLocalDateTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        LocalDateTime dateTime = LocalDateTime.parse(eventDateTime, DateTimeFormatter.ofPattern(pattern));
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));

    }
    // getter, setter
}
