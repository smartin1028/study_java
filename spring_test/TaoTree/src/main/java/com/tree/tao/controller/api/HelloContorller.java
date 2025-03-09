package com.tree.tao.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("v1")
public class HelloContorller {

    @GetMapping("/hello")
    public String hello(String name) {
        return String.format("hello %s", name);
    }
}
