package com.tree.tao.dto;

import com.tree.tao.entity.Blog;
import lombok.Data;

import java.time.LocalDateTime;

public class BlogDto {

    @Data
    public static class SearchVo{
        private Long id;

        private String title;
        private String content;
        private String author;

        private LocalDateTime createdAt;
        
        // 기본 생성자 막음
        private SearchVo(){
            
        }
        //
        public static SearchVo convertData(Blog blog) {
            SearchVo searchVo = new SearchVo();
            searchVo.id = blog.getId();
            searchVo.title = blog.getTitle();
            searchVo.content = blog.getContent();
            searchVo.author = blog.getAuthor();
            searchVo.createdAt = blog.getCreatedAt();
            return searchVo;
        }
    }

    @Data
    public static class createVo{
        private Long id;
        private String title;
        private String content;
        private String author;
        private LocalDateTime createdAt;
    }


    @Data
    public static class ReturnVo{
        private Long id;

        private String title;
        private String content;
        private String author;

        private LocalDateTime createdAt;
    }

}
