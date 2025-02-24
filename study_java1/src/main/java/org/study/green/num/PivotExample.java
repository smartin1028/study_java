package org.study.green.num;

import java.util.*;
import java.util.stream.*;


public class PivotExample {
    public static void main(String[] args) {

        // 판매 데이터 생성
        List<String> products = Arrays.asList("A", "B", "A", "B", "A", "B");
        List<String> categories = Arrays.asList("가전", "가전", "가구", "가구", "가전", "가구");
        List<Integer> sales = Arrays.asList(10, 15, 20, 25, 30, 35);

        // 피벗 테이블 생성
        Map<String, Map<String, Integer>> pivotTable = new HashMap<>();
        for (int i = 0; i < products.size(); i++) {
            String product = products.get(i);
            String category = categories.get(i);
            int sale = sales.get(i);

            /**
             * getOrDefault 함수는 Java의 Map 인터페이스에서 제공하는 메소드입니다.
             * 이 함수는 특정 키에 매핑된 값을 반환합니다.
             * 만약 맵에 키가 없는 경우에는 기본값을 반환합니다.
             * 이 메소드는 맵에 키가 없을 때 null 대신 기본값을 제공하므로 NullPointerException을 방지하는 데 도움이 됩니다.
             */
            Map<String, Integer> categoryMap = pivotTable.getOrDefault(product, new HashMap<>());
            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + sale);
            pivotTable.put(product, categoryMap);
        }

        // 피벗 테이블 출력
        for (String product : pivotTable.keySet()) {
            System.out.println(product + ": " + pivotTable.get(product));
        }
    }
}
