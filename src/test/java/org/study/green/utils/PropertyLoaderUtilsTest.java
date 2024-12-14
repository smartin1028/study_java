package org.study.green.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyLoaderUtilsTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {
        Properties properties = PropertyLoaderUtils.loadPropertiesByClassPath("config-test.properties");
//        String resourcePath = "config-test.properties";
//        try (InputStream input = PropertyLoaderUtilsTest.class.getClassLoader()
//                .getResourceAsStream(resourcePath)) {
//            if (input == null) {
//                log.info("Unable to find " + resourcePath);
//            }
//            properties.load(input);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
        // 시스템 프로퍼티로 등록
        properties.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void addPropertyList() {
        Properties properties = PropertyLoaderUtils.loadPropertiesByClassPath("config-test.properties");
        properties.forEach((key, value) -> {
            log.info("key = {} : value = {}", key, value);
        });
    }

    @Test
    public void allPropertyList() {
        Properties properties = System.getProperties();
        properties.forEach((key, value) -> {
            log.info("key = {} : value = {}", key, value);
        });
    }

    @Test
    public void getPropertyListByFilter() {
        Properties properties = System.getProperties();
        List<String> lst = new ArrayList<>();
        lst.add("user");

        properties.forEach((key, value) -> {
            for (String str : lst) {
                if (key.toString().contains(str)) {
                    log.info("{} = {}", key, value);
                }
            }
        });
    }
}