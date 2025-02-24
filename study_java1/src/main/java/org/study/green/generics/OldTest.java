package org.study.green.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OldTest {
    // stamp 인스턴스만 취급한다.
    /**
     * 아이템 26 로 타입은 사용하지 말라
     * 로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않는 때를 말한다
     * 아래와 같은 형식
     */
    private final Collection stamps = new ArrayList();

    public static void main(String[] args) {

        OldTest oldTest = new OldTest();
        oldTest.stamps.add(new Object());

        Object[] objects = new Long[1];
        objects[0] = "타입이 달라 넣을 수 없다";

        // List<Object> ol = new ArrayList<Long>(); // 컴파일 되지 않는다
    }
}
