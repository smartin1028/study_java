package org.study.green.generics;

import java.util.ArrayList;
import java.util.List;

public class MainBox {
    public static void main(String[] args) {
        GenericBox<Integer> integerBox = new GenericBox<>();
        integerBox.set(10);
        Integer i = integerBox.get();
        System.out.println("i = " + i);

        GenericBox<String> stringBox = new GenericBox<>();
        stringBox.set("Hello");
        String s = stringBox.get();
        System.out.println("stringBox = " + s);

        List<String> list = new ArrayList<>();

    }
}
