package com.tree.tao.controller.front;

import com.tree.tao.dto.BlogDto;
import com.tree.tao.entity.Blog;
import com.tree.tao.service.BlogService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/blog")
@AllArgsConstructor
public class BlogController {

    BlogService blogService;
    ModelMapper modelMapper;


    @GetMapping("/list")
    public String list(Model model) {
        // 블로그 글 목록 조회
        List<Blog> blogs = blogService.findAll();
        List<BlogDto.SearchVo> posts = blogs.stream().map(map -> modelMapper.map(map, BlogDto.SearchVo.class)).toList();
        model.addAttribute("posts", posts); // "posts"라는 이름으로 데이터 추가
        return "blog/list";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "blog/write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute BlogDto.createVo blogDto) {
        Blog blog = modelMapper.map(blogDto, Blog.class);
        blog.setCreatedAt(LocalDateTime.now());
        blogService.addBlogData(blog);
        return "redirect:/blog/list";
    }
}