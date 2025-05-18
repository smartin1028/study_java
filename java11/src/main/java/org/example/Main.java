package org.example;

import org.example.db.DBType;
import org.example.db.Oracle;
import org.example.db.SqlServer;
import org.tao.utils.XmlUtils;
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
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        System.out.println("Hello, World!");

        // -Ddb_ip=localhost -Ddb_port=1521 -Ddb_id=system -Ddb_password=password -DDBType=ORACLE
        // -Ddb_ip=localhost -Ddb_port=11433 -Ddb_id=SA -Ddb_password=password -DDBType=SQL_SERVER

        String property = System.getProperty("DBType", DBType.SQL_SERVER.name());
        DBType dbType = DBType.valueOf(property);


        if (dbType == DBType.ORACLE) {
            System.out.println("Oracle DB에 연결중");
            try (Connection conn = Oracle.getConnection(); Statement stmt = conn.createStatement()) {

                System.out.println("Oracle DB에 연결 완료");
                try (// 데이터 조회
                     ResultSet rs = stmt.executeQuery("SELECT 'hello' str FROM dual")) {
                    while (rs.next()) {
                        System.out.println("str: " + rs.getString("str"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (dbType == DBType.SQL_SERVER) {
            System.out.println("SqlServer DB에 연결중");
            try (Connection conn = SqlServer.getConnection(); Statement stmt = conn.createStatement()) {

                System.out.println("SqlServer DB에 연결 완료");
                try (// 데이터 조회
                     ResultSet rs = stmt.executeQuery("SELECT 'hello' str")) {
                    while (rs.next()) {
                        System.out.println("str: " + rs.getString("str"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        // testXml();
    }

    private static void testXml() throws ParserConfigurationException, TransformerException {
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

        transformer.transform(source, result);

        /// 문자열로 변경
        String s = XmlUtils.convertXmlToString(doc);
        XmlUtils.printXml(s);

        /// 그냥 출력
        XmlUtils.printXmlDoc(doc);
    }
}