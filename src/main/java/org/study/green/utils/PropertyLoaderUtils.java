package org.study.green.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoaderUtils {
    public static Properties loadPropertiesByClassPath(String resourcePath) {
        Properties properties = new Properties();
        try (InputStream input = PropertyLoaderUtils.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (input == null) {
                System.out.println("Unable to find " + resourcePath);
            }

            properties.load(input);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return properties;
    }
}
