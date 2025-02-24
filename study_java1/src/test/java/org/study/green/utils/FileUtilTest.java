package org.study.green.utils;

import org.junit.Assert;
import org.junit.Test;
import org.study.green.resource.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FileUtilTest {

    @Test
    public void testFileWrite(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
        // 파일 쓰기
        try (FileOutputStream fos = new FileOutputStream(fileName);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)
        ) {
            String text = "Hello World! 한글 ： こっちだよ";
            bw.write(text);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testFileRead(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
        // 파일 읽기
        try (FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileWriteByBuffered(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
        // 파일 쓰기
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            bw.write("Hello World! 한글");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testFileReadByBuffered(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
        // 파일 읽기
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileWriteByFiles(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
        // 파일 쓰기
        try {
            Files.write(Paths.get(fileName),
                    Arrays.asList("Hello 한글 ", "World"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testFileReadByFiles(){
        String resourceTestPath = ResourceUtils.getResourceTestPath();
        String fileName = FileUtil.appendPath(resourceTestPath, "output.txt");
// 파일 읽기
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testFileUtil() {
        String separator = FileSystems.getDefault().getSeparator();
        System.out.println("separator = " + separator);
        System.out.println("separator = " + File.separator);

    }

    @Test
    public void testFileUtil2() {
        String separator = File.separator;
        System.out.println("separator = " + separator);

        String s = "\\\\test\\aa".replaceAll("\\\\\\\\", separator+separator);
        System.out.println(s);

        String test = FileUtil.appendPath("test", "123");
        System.out.println("test = " + test);
        System.out.println(File.separator + File.separator);
    }

    @Test
    public void convertFilePath() {
        String separator = FileSystems.getDefault().getSeparator();
        System.out.println("separator = " + separator);

        String actual = FileUtil.convertFilePath("/test/aa");
        System.out.println("actual = " + actual);
        if (separator.equals("\\")) {
            System.out.println("This is a Windows operating system.");
            Assert.assertEquals("\\test\\aa", actual);
        } else if (separator.equals("/")) {
            System.out.println("This is a Unix or Linux operating system.");
            Assert.assertEquals("/test/aa", actual);
        } else {
            System.out.println("This is an unknown operating system.");
            Assert.assertEquals("/test/aa", actual);
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