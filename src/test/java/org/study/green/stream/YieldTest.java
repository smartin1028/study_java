package org.study.green.stream;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class YieldTest {

    @Test
    public void testYield() {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7");
        Stream<List<String>> chunksStream = Yield.getChunksStream(list, list.size() * 0.75);
        chunksStream.forEach(System.out::println);
//        chunksStream.forEach(x -> System.out.println(x));

//        long count = chunksStream.count();
//        System.out.println("count = " + count);

    }

}