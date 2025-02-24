package org.study.green.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Person 클래스
public class Person {
    private String name;
    private int age;
    private City city;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, City city) {
        this.name = name;
        this.city = city;
    }
}