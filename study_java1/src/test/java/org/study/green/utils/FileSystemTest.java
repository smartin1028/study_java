package org.study.green.utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileSystemTest {

    @Test
    public void fileSystemTest(){
        // Files.walk() 사용
        try (Stream<Path> paths = Files.walk(Paths.get("경로/폴더명"))) {
            paths.filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
