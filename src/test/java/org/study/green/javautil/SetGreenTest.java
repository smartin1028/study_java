package org.study.green.javautil;

import lombok.Data;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class SetGreenTest {

    @Test
    public void t_set_to_string_array_00() {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(0));
        }
        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(i));
        }

        System.out.println("set = " + set);
        System.out.println("set.size() = " + set.size());


        String[] array = set.toArray(new String[0]);
        for (String s : array) {
            System.out.println("s = " + s);
        }

        System.out.println("array.length = " + array.length);

    }

    @Test
    public void t_set_to_string_array_01() {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(0));
        }
        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(i));
        }

        System.out.println("set = " + set);
        System.out.println("set.size() = " + set.size());


        String[] array = set.toArray(new String[0]);
        for (String s : array) {
            System.out.println("s = " + s);
        }

        System.out.println("array.length = " + array.length);

    }

    @Test
    public void t_SetGreenTest_true_00() {
        List<Person> peopleWithDuplicates = Arrays.asList(
                new Person(1, "John"),
                new Person(2, "Jane"),
                new Person(1, "Jack"),
                new Person(2, "Jill"),
                new Person(3, "Jill")
        );

        Map<Integer, Person> peopleWithoutDuplicates = peopleWithDuplicates.stream()
                .collect(Collectors.toMap(
                        per -> per.getId(),      // 키 추출 함수
                        person -> person,   // 값 추출 함수
                        (existingValue, newValue) -> existingValue  // 두 값이 충돌할 때 사용할 병합 함수
                ));

        List<Person> listWithoutDuplicates = new ArrayList<>(peopleWithoutDuplicates.values());

        for (Person listWithoutDuplicate : listWithoutDuplicates) {
            System.out.println("listWithoutDuplicate = " + listWithoutDuplicate);
        }

    }

    @Data
    private class Person {
        private int id;
        private String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}