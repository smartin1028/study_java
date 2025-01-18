package org.study.green.utils;

import java.util.Properties;

public class PropertiesUtils {

    public static void printPropertiesByParam(Properties prop) {
        for (Object o : prop.keySet()) {
            String key = o.toString();
            String value = prop.getProperty(key);
            System.out.println(key + "=" + value);
        }
    }

    public static void printPropertiesBySystem() {
        PropertiesUtils.printPropertiesByParam(System.getProperties());
    }


}
