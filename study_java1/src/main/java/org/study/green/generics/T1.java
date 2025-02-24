package org.study.green.generics;

import java.util.concurrent.ThreadLocalRandom;

public class T1 {
    public static void main(String[] args) {
        ThreadLocalRandom current = ThreadLocalRandom.current();

        System.out.println("current = " + current);
        System.out.println("current.nextInt(10) = " + current.nextInt(10));
    }
}
