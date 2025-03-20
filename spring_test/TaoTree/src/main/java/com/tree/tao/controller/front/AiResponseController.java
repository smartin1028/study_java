package com.tree.tao.controller.front;

import com.tree.tao.dto.AiResponseHistoryDto;
import com.tree.tao.dto.BlogDto;
import com.tree.tao.entity.AiResponseHistory;
import com.tree.tao.entity.Blog;
import com.tree.tao.repository.AiResponseHistoryRepository;
import com.tree.tao.service.AiHistoryService;
import com.tree.tao.service.BlogService;
import lombok.AllArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ai/hist")
@AllArgsConstructor
@Transactional(readOnly = true)
public class AiResponseController {

    AiHistoryService aiHistoryService;
    AiResponseHistoryRepository aiResponseHistoryRepository;
    ModelMapper modelMapper;

    @GetMapping("list")
    public String list(Model model) {
        // 블로그 글 목록 조회
        List<AiResponseHistory> all = aiHistoryService.findAll();
        List<AiResponseHistoryDto.SearchVo> posts = all.stream().map(map -> modelMapper.map(map, AiResponseHistoryDto.SearchVo.class)).toList();
        model.addAttribute("posts", posts); // "posts"라는 이름으로 데이터 추가
        return "ai/hist/list";
    }

    @GetMapping("{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Optional<AiResponseHistory> byId = aiResponseHistoryRepository.findById(id);
        if(byId.isPresent()) {
            AiResponseHistory aiResponseHistory = byId.get();
        }else{
            throw new IllegalArgumentException("데이터를 찾을 수 없습니다.");
        }
        model.addAttribute("post", modelMapper.map(byId, AiResponseHistoryDto.SearchVo.class));
        return "ai/hist/view";  // view.html 템플릿으로 이동
    }

}