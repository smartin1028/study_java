package com.tree.tao.repository;

import com.tree.tao.entity.Blog;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class BlogRepositoryTest {

    @Autowired
    BlogRepository blogRepository;

    @Test
    public void test(){

        Blog entity = new Blog();
        blogRepository.save(entity);
        blogRepository.flush();

    }

    @Test
    void createBlogPosts() {
        // 첫 번째 블로그 포스트
        Blog post1 = new Blog();
        post1.setTitle("Spring Boot 시작하기");
        post1.setContent("Spring Boot는 스프링 프레임워크를 쉽게 사용할 수 있게 해줍니다.");
        post1.setAuthor("김개발");
        post1.setCreatedAt(LocalDateTime.now());
        blogRepository.save(post1);

        // 두 번째 블로그 포스트
        Blog post2 = new Blog();
        post2.setTitle("JPA 학습하기");
        post2.setContent("JPA를 사용하면 객체 지향적인 데이터베이스 관리가 가능합니다.");
        post2.setAuthor("이자바");
        post2.setCreatedAt(LocalDateTime.now());
        blogRepository.save(post2);

        // 세 번째 블로그 포스트
        Blog post3 = new Blog();
        post3.setTitle("Thymeleaf 템플릿 엔진");
        post3.setContent("Thymeleaf는 네츄럴 템플릿 엔진으로 HTML을 그대로 유지하면서 템플릿 기능을 사용할 수 있습니다.");
        post3.setAuthor("박프론트");
        post3.setCreatedAt(LocalDateTime.now());
        blogRepository.save(post3);

        List<Blog> all = blogRepository.findAll();
        for (Blog blog : all) {
            log.info("blog {}", blog);
        }
    }

}