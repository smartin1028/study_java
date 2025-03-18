package com.tree.tao.service;

import com.tree.tao.dto.AiResponseHistoryDto;
import com.tree.tao.entity.AiResponseHistory;
import com.tree.tao.repository.AiResponseHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AiHistoryService {

    private final AiResponseHistoryRepository aiResponseHistoryRepository;

    public List<AiResponseHistory> findAll() {
        return aiResponseHistoryRepository.findAll();
    }

    @Transactional
    public AiResponseHistory addAiHistory(AiResponseHistoryDto.createVo createVo) {
        AiResponseHistory aiResponseHistory = AiResponseHistoryDto.convert(createVo);
        return aiResponseHistoryRepository.save(aiResponseHistory);
    }
}