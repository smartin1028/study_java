package com.tree.tao.entity;

import com.tree.tao.utils.MyEncryptDecrypt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "TB_BLOG")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String author;

    @CreatedDate
    private LocalDateTime createdAt;


    public String getContent() {
        try {
            return MyEncryptDecrypt.decrypt(content);
        } catch (Exception e) {
            return content;
        }
    }

    public void setContent(String content) {
        try {
            this.content = MyEncryptDecrypt.encrypt(content);
        } catch (Exception e) {
            this.content = content;
        }
    }
}
