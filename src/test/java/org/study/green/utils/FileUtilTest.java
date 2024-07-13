package org.study.green.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;

import static org.junit.Assert.*;

public class FileUtilTest {

    @Test
    public void convertFilePath() {
        String separator = FileSystems.getDefault().getSeparator();
        if (separator.equals("\\\\")) {
            System.out.println("This is a Windows operating system.");
            Assert.assertEquals(FileUtil.convertFilePath("\\test\\aa"), "\\test\\aa");
        } else if (separator.equals("/")) {
            System.out.println("This is a Unix or Linux operating system.");
            Assert.assertEquals(FileUtil.convertFilePath("\\test\\aa"), "/test/aa");
        } else {
            System.out.println("This is an unknown operating system.");
            Assert.assertEquals(FileUtil.convertFilePath("\\test\\aa"), "/test/aa");
        }
    }

    @Test
    public void appendPath() {
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("os = " + os);


    }

    @Test
    public void t_os_name() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            System.out.println("This is a Windows operating system.");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            System.out.println("This is a Unix or Linux operating system.");
        } else {
            System.out.println("This is an unknown operating system.");
        }

    }
}