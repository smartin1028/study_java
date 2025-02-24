package org.study.green;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OriginalIndexSorting {

    public static void main(String[] args) {
        OriginalIndexSorting sorting = new OriginalIndexSorting();
        sorting.arr();
        sorting.array();
        
    }

    private void array() {
        System.out.println("OriginalIndexSorting.array");
        List<Integer> arr = new ArrayList<>(Arrays.asList(5, 2, 3, 1, 4));
        int length = arr.size();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            indices.add(i);
        }
//        indices.sort((a, b) -> arr.get(a) - arr.get(b));
        indices.sort(Comparator.comparingInt(arr::get));
//        indices.sort(Comparator.comparingInt(arr::get).reversed());
        for (Integer i : indices) {
            System.out.println("숫자 " + arr.get(i) + "의 원래 위치: " + i);
        }


    }

    public void arr() {
        System.out.println("OriginalIndexSorting.arr");
        Integer[] arr = {5, 2, 3, 1, 4};  // 무작위로 섞인 숫자 배열
        int length = arr.length;
        Integer[] indices = new Integer[length];

        // 원래 인덱스를 초기화합니다.
        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }

        // 원래 인덱스와 함께 오름차순으로 정렬합니다.
        Arrays.sort(indices, (a, b) -> arr[a] - arr[b]);

        // 각 숫자의 원래 위치와 함께 배열을 출력합니다.
        for (int i = 0; i < length; i++) {
            System.out.println("숫자 " + arr[indices[i]] + "의 원래 위치: " + indices[i]);
        }
    }


}
