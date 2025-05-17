package org.tao;


import org.junit.Test;

import java.util.Objects;

public class PersonTest {

    @Test(expected = NullPointerException.class)
    public void testAdd() {
        System.out.println("true = " + true);
        Objects.requireNonNull(null, "null을 입력할 수 없습니다");
    }
}