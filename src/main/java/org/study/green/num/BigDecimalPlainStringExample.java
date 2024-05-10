package org.study.green.num;

import java.math.BigDecimal;

public class BigDecimalPlainStringExample {
    public static void main(String[] args) {
        showData(new BigDecimal("1e+3"));
        showData(new BigDecimal("1e-10"));
//        showData(new BigDecimal("1+10")); error

    }

    private static void showData(BigDecimal number2) {
        System.out.println("Number in E notation: " + number2);
        System.out.println("Number in plain string: " + number2.toPlainString());
    }
}
