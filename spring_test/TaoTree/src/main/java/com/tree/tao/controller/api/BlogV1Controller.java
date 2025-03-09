package com.tree.tao.controller.api;

import com.tree.tao.dto.BlogDto;
import com.tree.tao.entity.Blog;
import com.tree.tao.repository.BlogRepository;
import com.tree.tao.service.BlogService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RestController
@AllArgsConstructor
@Transactional(readOnly = true)
public class BlogV1Controller {

    BlogService blogService;
    ModelMapper modelMapper;
    BlogRepository blogRepository;

    @GetMapping("/blog/v1/{id}")
    public ResponseEntity<Object> getBlog(@PathVariable Long id) {
        Optional<Blog> byId = blogRepository.findById(id);
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
//
//    @GetMapping("/write")
//    public String writeForm() {
//        return "blog/write";
//    }
//
//    @PostMapping("/write")
//    public String write(@ModelAttribute BlogDto.createVo blogDto) {
//        Blog blog = modelMapper.map(blogDto, Blog.class);
//        blog.setCreatedAt(LocalDateTime.now());
//        blogService.addBlogData(blog);
//        return "redirect:/blog/list";
//    }
}