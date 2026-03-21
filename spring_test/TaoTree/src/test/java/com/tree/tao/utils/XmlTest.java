package com.tree.tao.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.ToString;
import org.hibernate.type.format.jackson.JacksonXmlFormatMapper;
import org.junit.jupiter.api.Test;

public class XmlTest {

    @Test
    public void t_XmlTest_true_00() throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xml = xmlMapper.writeValueAsString(new Person("P001", "Alice", 30));
        System.out.println("xml = " + xml);
        Person p2 = xmlMapper.readValue(xml, Person.class);
        System.out.println("p2 = " + p2);
    }

    @ToString
    @Data
    @JacksonXmlRootElement(localName = "person")
    public static class Person {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        private String name;
        private int age;

        public Person() {
        }

        public Person(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }
}
