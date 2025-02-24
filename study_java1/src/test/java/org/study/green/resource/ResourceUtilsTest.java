package org.study.green.resource;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

@Slf4j
public class ResourceUtilsTest {

    @Before
    public void setUp() throws Exception {
        log.info("setUp");
    }

    @After
    public void tearDown() throws Exception {
        log.info("tearDown");
    }

    @Test
    public void systemPath() {
        String separator = File.separator;
        System.out.println("separator = " + separator);


        String originalPath = "path/to/your\\\\file";
        String convertedPath = originalPath.replace("/", File.separator).replace("\\\\", File.separator);
        System.out.println("Converted Path: " + convertedPath);

    }

    @Test
    public void getClassResourcePath() {
        log.info("getClassResourcePath");
        String classResourcePath = ResourceUtils.getClassResourcePath();
        log.info("classResourcePath: {}", classResourcePath);
    }

    @Test
    public void getRootPath() {
        log.info("getRootPath");
        String classResourcePath = ResourceUtils.getRootPath();
        log.info("getRootPath: {}", classResourcePath);
    }

    @Test
    public void getResourcePath() {
        log.info("getResourcePath");
        String resourcePath = ResourceUtils.getResourcePath();
        log.info("getResourcePath: {}", resourcePath);
    }

    @Test
    public void getResourceTestPath() {
        log.info("getResourceTestPath");
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        log.info("getResourceTestPath: {}", resourceTestPath);
    }
}