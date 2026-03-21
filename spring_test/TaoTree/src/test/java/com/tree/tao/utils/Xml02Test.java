package com.tree.tao.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class Xml02Test {

    @Test
    public void t_XmlTest_true_00() throws JsonProcessingException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        Book bookObj = new Book();
        bookObj.isbn = "1234567890";
        bookObj.title = "Java EE 8";
        System.out.println("bookObj = " + bookObj);
        marshaller.marshal(bookObj, System.out);
        System.out.println("bookObj = " + bookObj);

        // StringWriter 사용
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(bookObj, stringWriter);

        // 문자열 변수에 결과 저장
        String xmlString = stringWriter.toString();
        System.out.println("xmlString = " + xmlString);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        bookObj = (Book) unmarshaller.unmarshal(new StringReader(xmlString));

        System.out.println("bookObj = " + bookObj);
    }

    @ToString
//    @Data
    @XmlRootElement(name = "book")
    public static class Book {
        @XmlElement
        private String title;
        @XmlAttribute
        private String isbn;
    }

}
