package org.tao.utils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class DOMParserExample {
    public static void main(String[] args) {
        try {

            // ClassLoader를 사용하여 리소스 로드
            ClassLoader classLoader = DOMParserExample.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("employee.xml");

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    System.out.println("직원 ID: " + id);
                    System.out.println("이름: " + name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}