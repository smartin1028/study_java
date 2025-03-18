package com.tree.tao.controller.api;

import com.tree.tao.dto.EventDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RestController
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventV1Controller {

    @PostMapping("/event/v1/datatime")
    public ResponseEntity<String> createEvent(@RequestBody EventDTO eventDTO) {
        // 비즈니스 로직 처리
        return ResponseEntity.ok("정보 " + eventDTO.getEventDateTime() + "\t " + eventDTO.convertToLocalDateTime());
    }
}