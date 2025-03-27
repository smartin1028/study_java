package org.tao.utils.xml;

import javax.xml.stream.*;

public class StAXParserExample {
    public static void main(String[] args) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(
                    StAXParserExample.class.getClassLoader().getResourceAsStream("output.xml")
            );

            while(reader.hasNext()) {
                int event = reader.next();

                switch(event) {
                    case XMLStreamConstants.START_ELEMENT:
                        System.out.println("시작 엘리먼트: " + reader.getLocalName());
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        String text = reader.getText().trim();
                        if(!text.isEmpty()) {
                            System.out.println("텍스트: " + text);
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        System.out.println("종료 엘리먼트: " + reader.getLocalName());
                        break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}