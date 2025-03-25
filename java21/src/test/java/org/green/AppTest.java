package org.green;

import org.junit.Test;
import org.tao.Calculator;

public class AppTest {

    @Test
    public void testMyLib01() {
        Calculator calculator = new Calculator();
        int add = calculator.add(5, 3);
        // Java 21의 새로운 문자열 템플릿 기능 사용
        String result = String.format("계산 결과 : %s", add);// STR."계산 결과: \{calculator.add(5, 3)}";
        System.out.println(result);
    }
}