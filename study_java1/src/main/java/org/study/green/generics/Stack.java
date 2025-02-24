package org.study.green.generics;


import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

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
            System.out.println("elements.length  = " + elements.length);
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

        DelayQueue<Delayed> delayeds = new DelayQueue<>();
        Delayed delayedItem = new DelayedItem(3000); // 3초 뒤에 사용 가능
        delayeds.put(delayedItem);

        int size1 = delayeds.size();
        System.out.println("size1 = " + size1);

        try {
            // take() 메소드는 요소가 사용 가능해질 때까지 대기합니다.
            Delayed item = delayeds.take();
            System.out.println("item = " + item);
            size1 = delayeds.size();
            System.out.println("size1 = " + size1);
//            System.out.println("item.item = " + item.item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
