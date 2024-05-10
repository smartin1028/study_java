package org.study.green.num;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalExamples {

    public static void main(String[] args) {
        BigDecimalExamples examples = new BigDecimalExamples();
        examples.initializeBigDecimal();
        examples.addTwoBigDecimals();
        examples.subtractTwoBigDecimals();
        examples.multiplyTwoBigDecimals();
        examples.divideTwoBigDecimals();
        examples.compareTwoBigDecimals();
        examples.computeExponentWithBigDecimal();
        examples.ExponentExample();
    }

    // BigDecimal 초기화
    public void initializeBigDecimal() {
        BigDecimal number = new BigDecimal("10.15");
        System.out.println("Number: " + number);
    }

    // 두 BigDecimal 값의 덧셈
    public void addTwoBigDecimals() {
        BigDecimal num1 = new BigDecimal("10.15");
        BigDecimal num2 = new BigDecimal("20.25");
        BigDecimal sum = num1.add(num2);
        System.out.println("Sum: " + sum);
    }

    // 두 BigDecimal 값의 뺄셈
    public void subtractTwoBigDecimals() {
        BigDecimal num1 = new BigDecimal("10.15");
        BigDecimal num2 = new BigDecimal("20.25");
        BigDecimal difference = num1.subtract(num2);
        System.out.println("Difference: " + difference);
    }

    // 두 BigDecimal 값의 곱셈
    public void multiplyTwoBigDecimals() {
        BigDecimal num1 = new BigDecimal("10.15");
        BigDecimal num2 = new BigDecimal("20.25");
        BigDecimal product = num1.multiply(num2);
        System.out.println("Product: " + product);
    }

    // 두 BigDecimal 값의 나눗셈
    public void divideTwoBigDecimals() {
        BigDecimal num1 = new BigDecimal("10.15");
        BigDecimal num2 = new BigDecimal("20.25");
        // 2는 소수점 이하 자릿수, RoundingMode.HALF_UP은 반올림 방식을 의미합니다.
        BigDecimal quotient = num1.divide(num2, 2, RoundingMode.HALF_UP);
        System.out.println("Quotient: " + quotient);
    }

    // BigDecimal 값의 비교
    public void compareTwoBigDecimals() {
        BigDecimal num1 = new BigDecimal("10.15");
        BigDecimal num2 = new BigDecimal("20.25");
        // num1이 num2보다 작으면 -1, 같으면 0, 크면 1을 반환합니다.
        int comparison = num1.compareTo(num2);
        System.out.println("Comparison Result: " + comparison);
    }

    // BigDecimal로 지수 계산
    public void computeExponentWithBigDecimal() {
        BigDecimal base = new BigDecimal("10");
        BigDecimal exponent = new BigDecimal("2");
        // 10^2을 계산합니다.
        BigDecimal result = base.pow(exponent.intValue());
        System.out.println("Exponent Result: " + result);
    }

    public void ExponentExample() {
        BigDecimal base = new BigDecimal("10");
        BigDecimal exponent = new BigDecimal("3");
        BigDecimal result = base.pow(exponent.intValue());

        result = result.setScale(6, BigDecimal.ROUND_HALF_UP); // 소수점 아래 6자리까지 표현하고, 반올림합니다.

        System.out.println("Result: " + result);
    }

}
