package org.study.green.generics;


import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public Stack() {
        this.elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    public void push(E o) {
        ensureCapacity();
        elements[size++] = o;
    }
    
    public E pop() {
        if (size == 0) throw new EmptyStackException();
        E o = (E) elements[--size];
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
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < 35; i++) {
            stack.push("Hello");

        }
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.size = " + stack.size);
        
    }

}
