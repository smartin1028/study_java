package org.study.green.javautil;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SetGreenTest {

    @Test
    public void t_set_to_string_array_00() {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(0));
        }
        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(i));
        }

        System.out.println("set = " + set);
        System.out.println("set.size() = " + set.size());


        String[] array = set.toArray(new String[0]);
        for (String s : array) {
            System.out.println("s = " + s);
        }

        System.out.println("array.length = " + array.length);

    }

    @Test
    public void t_set_to_string_array_01() {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(0));
        }
        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(i));
        }

        System.out.println("set = " + set);
        System.out.println("set.size() = " + set.size());


        String[] array = set.toArray(new String[0]);
        for (String s : array) {
            System.out.println("s = " + s);
        }

        System.out.println("array.length = " + array.length);

    }
}