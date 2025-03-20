package org.example;

//import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Marshaller;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class PersonTest {

    @Test
    public void test() throws JAXBException, UnsupportedEncodingException {
        Person person = new Person();
        JAXBContext context = JAXBContext.newInstance(Person.class);
        Marshaller marshaller = context.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE); // XML 선언 자체를 제거
//        marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE); // XML 선언 제거

        marshaller.marshal(person, new File("person.xml"));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(person, baos);
        String xmlString = baos.toString(StandardCharsets.UTF_8);
        System.out.println("xmlString = " + xmlString);
    }
}