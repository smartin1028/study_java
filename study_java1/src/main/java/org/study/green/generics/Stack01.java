package org.study.green.generics;


import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack01 {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack01() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    public void push(Object o) {
        ensureCapacity();
        elements[size++] = o;
    }
    
    public Object pop() {
        if (size == 0) throw new EmptyStackException();
        Object o = elements[--size];
        elements[size] = null;
        return o;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            int newLength = size * 2 + 1;
            System.out.println("newLength = " + newLength);
            System.out.println("elements.length  = " + elements.length );
            elements = Arrays.copyOf(elements, newLength);
        }
    }

    public static void main(String[] args) {
        Stack01 stack = new Stack01();
        for (int i = 0; i < 35; i++) {
            stack.push("Hello");

        }
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.size = " + stack.size);
        
    }

}
