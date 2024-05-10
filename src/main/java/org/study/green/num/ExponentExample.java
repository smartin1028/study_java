package org.study.green.num;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExponentExample {
    /**
     * 이 코드는 먼저 Math.abs 함수를 사용하여 지수를 절댓값으로 변환합니다.
     * 따라서 base.pow(Math.abs(exponent.intValue())) 부분은 10의 10제곱,
     * 즉 1e10을 계산합니다. 그런 다음 BigDecimal.ONE.divide(positiveExponentResult, 10, BigDecimal.ROUND_HALF_UP)
     * 부분은 이 결과를 1로 나누어서 원하는 1e-10을 얻습니다.
     * 10은 소수점 아래 자릿수를, BigDecimal.ROUND_HALF_UP은 반올림 방식을 지정합니다.
     * @param args
     */
    public static void main(String[] args) {
        BigDecimal base = new BigDecimal("10");
        BigDecimal exponent = new BigDecimal("-10");
        // 양수 지수로 계산
        BigDecimal positiveExponentResult = base.pow(Math.abs(exponent.intValue()));
        // 계산 결과를 1로 나눔
        BigDecimal result = BigDecimal.ONE.divide(positiveExponentResult, 10, RoundingMode.HALF_UP);
        System.out.println("Result: " + result);
        System.out.println("Result: " + result.doubleValue());
    }
}