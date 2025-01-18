package org.study.green.str;

public class PrintOut {

    public PrintOut() {
        int age = 20; // 정수형 변수
        double height = 180.5; // 부동 소수점 변수
        String name = "Kim"; // 문자열 변수

        // printf 메서드를 사용하여 변수를 포맷팅하고 출력합니다.
        System.out.printf("이름: %s, 나이: %d, 키: %.1f cm", name, age, height);
        System.out.println();
        System.out.printf("이름: %s, 나이: %s, 키: %s cm", name, age, height);
        System.out.println();
    }

    public void intTest() {
        int test = 31;
        String format = String.format("%05d", test);
        System.out.println(format);

    }

    public void strTest() {
        String test = "31";
        String format = StrUtil.fillZeroString(test, 5 , true);
        System.out.println(format);

    }
}
