package com.tree.tao.service;

import com.tree.tao.entity.Blog;
import com.tree.tao.repository.BlogRepository;
import lombok.AllArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Transactional
    public Blog addBlogData(Blog blog) {
        return blogRepository.save(blog);
    }

//    // 페이징 처리를 원한다면
//    public Page<Blog> findAll(Pageable pageable) {
//        return blogRepository.findAll(pageable);
//    }
}