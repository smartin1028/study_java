package org.study.green.stream;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Yield {

//    public <T> Stream<List<T>> getChunksStream(List<T> list, int size) {
//        int numChunks = (int) Math.ceil((double) list.size() / size);
//        return IntStream.range(0, numChunks)
//                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())));
//    }

    public static <T> Stream<List<T>> getChunksStream(List<T> list, double size) {
        int numChunks = (int) Math.ceil(list.size() / size);
        return IntStream.range(0, numChunks)
                .mapToObj(i -> list.subList((int) (i * size), Math.min((int) ((i + 1) * size), list.size())));
    }

    public Yield() {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7");
        getChunksStream(list, list.size()*0.75).forEach(System.out::println);

        IntStream.range(0, 5).mapToObj(i ->
                        {
                            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
                            objectObjectHashMap.put("test" + i, i);
                            return objectObjectHashMap;

                        }
                )
                .forEach(map -> System.out.println(map));

        Object put = new HashMap<>().put("1", 1);
        System.out.println("put = " + put);

        IntStream.range(0, 5).forEach(System.out::println);
    }

    public static void main(String[] args) {
        new Yield();
    }

}



