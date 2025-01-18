package org.study.green.stream;

import org.junit.Test;
import org.study.green.stream.vo.City;
import org.study.green.stream.vo.Person;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

public class StreamGroupTest {

    @Test
    public void maintest() {
        StreamGroup streamGroup = new StreamGroup();
    }

    private List<Person> getPeopleList() {

        List<Person> people = new ArrayList<>();

        // Person 클래스 데이터 샘플
        people.add(new Person("Kim", City.SEOUL));
        people.add(new Person("Lee", City.BUSAN));
        people.add(new Person("Park", City.SEOUL));
        people.add(new Person("Choi", City.BUSAN));
        people.add(new Person("Jung", City.SEOUL));
        people.add(new Person("Han", City.SEOUL));
        people.add(new Person("Kang", City.BUSAN));
        people.add(new Person("Yoon", City.SEOUL));
        people.add(new Person("Cho", City.BUSAN));
        people.add(new Person("Jang", City.SEOUL));
        people.add(new Person("Lim", City.SEOUL));
        people.add(new Person("Yang", City.BUSAN));
        people.add(new Person("Moon", City.SEOUL));
        people.add(new Person("Ha", City.BUSAN));
        people.add(new Person("Jeon", City.SEOUL));
        people.add(new Person("Oh", City.SEOUL));
        people.add(new Person("Ryu", City.BUSAN));
        people.add(new Person("Hwang", City.SEOUL));
        people.add(new Person("Kim", City.BUSAN));
        people.add(new Person("Lee", City.SEOUL));

        return people;
    }

    @Test
    public void t_simple_test() {
        List<Person> list = Arrays.asList(
                new Person("John", 20),
                new Person("Jane", 30),
                new Person("John", 40),
                new Person("Jane", 50)
        );

        Map<String, List<Person>> groupedByName = list.stream()
                .collect(groupingBy(Person::getName));

        groupedByName.forEach((name, personList) -> {
            System.out.println("Name: " + name);
            personList.forEach(person -> System.out.println("Age: " + person.getAge()));
        });
    }

    @Test
    public void t_StreamGroupTest_toSet() {
        List<Person> peopleList = getPeopleList();

        Map<City, Set<String>>
                namesByCity =
                peopleList.stream()
                        .collect(
                                groupingBy(
                                        person -> person.getCity(),
                                        mapping(Person::getName, toSet())
                                )
                        );

        namesByCity.forEach((name, list) -> {
            System.out.println("name = " + name);
            System.out.println("list = " + list);
        });
    }

    @Test
    public void t_StreamGroupTest_StringJoin() {
        List<Person> peopleList = getPeopleList();

        Map<City, String>
                namesByCity =
                peopleList.stream()
                        .collect(
                                groupingBy(
//                                        person -> person.getCity(),
                                        Person::getCity,
                                        mapping(Person::getName,
                                                Collectors.collectingAndThen(toSet(), set -> String.join(", ", set))
                                        )
                                )
                        );

        namesByCity.forEach((name, list) -> {
            System.out.println("name = " + name);
            System.out.println("list = " + list);
        });
    }
}