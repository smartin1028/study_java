package com.tree.tao.controller.api;

import com.tree.tao.dto.AiResponseHistoryDto;
import com.tree.tao.dto.BlogDto;
import com.tree.tao.entity.AiResponseHistory;
import com.tree.tao.entity.Blog;
import com.tree.tao.repository.AiResponseHistoryRepository;
import com.tree.tao.service.AiHistoryService;
import com.tree.tao.service.BlogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RestController
@AllArgsConstructor
@Transactional(readOnly = true)
public class AiHistoryV1Controller {

    BlogService blogService;
    AiHistoryService aiHistoryService;
    ModelMapper modelMapper;
    AiResponseHistoryRepository repository;

    @GetMapping("/ai_history/v1/{id}")
    public ResponseEntity<Object> getBlog(@PathVariable Long id) {
        Optional<AiResponseHistory> byId = repository.findById(id);
        BlogDto.ReturnVo returnVo;
        Map<String, Object> response = new HashMap<>();
        if (byId.isPresent()) {
//            returnVo = BlogDto.ReturnVo.convertData(byId.get());
            returnVo = modelMapper.map(byId, BlogDto.ReturnVo.class);
            response.put("data", returnVo);
            response.put("status", "success");
        }else{
            response.put("status", "failed");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ai_history/v1/write")
    public ResponseEntity<Object> write(@RequestBody AiResponseHistoryDto.createVo aiHistoryDto) {

        log.info("{}", aiHistoryDto);

        AiResponseHistory aiResponseHistory = aiHistoryService.addAiHistory(aiHistoryDto);

        Optional<AiResponseHistory> byId = repository.findById(aiResponseHistory.getId());
        Map<String, Object> response = new HashMap<>();
        if (byId.isPresent()) {
            response.put("data", modelMapper.map(byId, AiResponseHistoryDto.createVo.class));
            response.put("status", "success");
        }else{
            response.put("status", "failed");
        }

        return ResponseEntity.ok(response);
    }
}