package org.tao.utils.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SAXParserExample extends DefaultHandler {
    public static void main(String[] args) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SAXParserExample handler = new SAXParserExample();
            saxParser.parse(SAXParserExample.class.getClassLoader().getResourceAsStream("output.xml"), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        System.out.println("시작 엘리먼트: " + qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        System.out.println("종료 엘리먼트: " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        System.out.println("텍스트: " + new String(ch, start, length).trim());
    }
}