package org.tao.utils;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Xml 관련된 util
 */
public class XmlUtils {
    /**
     * XML Document를 String으로 변환
     */
    public static String convertXmlToString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // 출력 포맷 설정
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException("XML 변환 중 오류 발생", e);
        }
    }

    /**
     * XML String 출력 (예쁘게 출력)
     */
    public static void printXml(String xml) {
        System.out.println("==== XML 출력 시작 ====");
        System.out.println(xml);
        System.out.println("==== XML 출력 종료 ====");
    }

    /**
     * XML Document 출력
     */
    public static void printXmlDoc(Document doc) {
        String xmlString = convertXmlToString(doc);
        printXml(xmlString);
    }
}