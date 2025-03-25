package org.tao;

import org.junit.Test;
import org.tao.xml.Dependency;
import org.tao.xml.Project;
import org.tao.xml.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class XmlExample {
    @Test
    public void xmlSample01() throws JAXBException, XMLStreamException {
       // Create the object structure
        Project project = new Project();
        project.setXmlns("http://maven.apache.org/POM/4.0.0");
        project.setXmlnsXsi("http://www.w3.org/2001/XMLSchema-instance");
        project.setSchemaLocation("http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");

        project.setModelVersion("4.0.0");
        project.setGroupId("org.tao");
        project.setArtifactId("java7");
        project.setVersion("1.0-SNAPSHOT");

        // Set up dependency
        Dependency junit = new Dependency();
        junit.setGroupId("junit");
        junit.setArtifactId("junit");
        junit.setVersion("4.13.2");
        junit.setScope("test");

        List<Dependency> dependencies = new ArrayList<>();
        dependencies.add(junit);
        project.setDependencies(dependencies);

        // Set up properties
        Properties props = new Properties();
        props.setMavenCompilerSource("7");
        props.setMavenCompilerTarget("7");
        props.setProjectBuildSourceEncoding("UTF-8");
        project.setProperties(props);

        // Marshal to XML
        JAXBContext context = JAXBContext.newInstance(Project.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 출력할때 보기편함
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        // 출력을 받음
        StringWriter sw = new StringWriter();
        marshaller.marshal(project, sw);
        String xmlContent = sw.toString();

        // XML 선언을 추가한 최종 XML 문자열
        String finalXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlContent;
        System.out.println(finalXml);

    }

}
