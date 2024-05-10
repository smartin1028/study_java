package org.study.green.stream;

import java.util.*;

public class AggregateFunctionsTest {
    public static void main(String[] args) {

        AggregateFunctionsTest functionsTest = new AggregateFunctionsTest();
        functionsTest.arrTest();
        functionsTest.ArrayListTest();

    }
    public void arrTest() {
        System.out.println(" arrTest ");
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // sum
        int sum = Arrays.stream(numbers).sum();
        System.out.println("Sum: " + sum);

        // max
        OptionalInt max = Arrays.stream(numbers).max();
        if (max.isPresent()) {
            System.out.println("Max: " + max.getAsInt());
        }

        // min
        OptionalInt min = Arrays.stream(numbers).min();
        if (min.isPresent()) {
            System.out.println("Min: " + min.getAsInt());
        }

        // total
        long total = Arrays.stream(numbers).count();
        System.out.println("Total: " + total);

        // average
        OptionalDouble average = Arrays.stream(numbers).average();
        if (average.isPresent()) {
            System.out.println("Average: " + average.getAsDouble());
        }
    }


    public void ArrayListTest() {
        System.out.println(" ArrayListTest ");
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // sum
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum: " + sum);

        // max
        OptionalInt max = numbers.stream().mapToInt(Integer::intValue).max();
        if (max.isPresent()) {
            System.out.println("Max: " + max.getAsInt());
        }

        // min
        OptionalInt min = numbers.stream().mapToInt(Integer::intValue).min();
        if (min.isPresent()) {
            System.out.println("Min: " + min.getAsInt());
        }

        // total
        long total = numbers.size();
        System.out.println("Total: " + total);

        // average
        OptionalDouble average = numbers.stream().mapToInt(Integer::intValue).average();
        if (average.isPresent()) {
            System.out.println("Average: " + average.getAsDouble());
        }
    }
}
