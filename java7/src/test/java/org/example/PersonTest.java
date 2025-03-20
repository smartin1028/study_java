package org.example;


import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class PersonTest {

    @Test
    public void test() throws JAXBException {
        Person person = new Person();
        JAXBContext context = JAXBContext.newInstance(Person.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(person, new File("person.xml"));
    }
}