package org.tao.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        System.out.println("Hello, World!");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("root");
        doc.appendChild(root);

        Element child = doc.createElement("child");
        child.appendChild(doc.createTextNode("Hello XML"));
        root.appendChild(child);

        // XML 파일로 저장
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("output.xml"));

        transformer.transform(source, result);

        /// 문자열로 변경
        String s = XmlUtils.convertXmlToString(doc);
        XmlUtils.printXml(s);

        /// 그냥 출력
        XmlUtils.printXmlDoc(doc);
    }
}