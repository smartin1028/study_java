package org.study.green.utils;

import lombok.extern.slf4j.Slf4j;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TableSawUtil {

    public static void showTableSawInfo(Table table) {
        log.info("\ntable name = {}", table.name());
        log.info("\ntable data = {}", table.print());
        log.info("\ntable rows = {} x cols = {} ", table.rowCount(), table.columnCount());
        log.info("\ntable structure = {}", table.structure());

    }

    /**
     * 컬럼 이름이 있는지 확인
     *
     * @param table 테이블
     * @param colNm 확인할 컬럼명
     * @return true | false
     */
    public static boolean hasColumnName(Table table, String colNm) {
        if(table == null || colNm == null) return false;
        if(table.columnNames().isEmpty()) return false;
        return table.columnNames().contains(colNm);
    }

    /**
     * 필요한 컬럼만 추출해서 테이블로 반환하는 함수 - selectColumns 이용
     * @param t      복사할 테이블
     * @param colNms 복사할 컬럼명
     * @return new table
     */
    public static Table createTableWithColumns(Table t, String... colNms) {
        if (t == null) throw new IllegalArgumentException("table is null");
        return t.selectColumns(colNms);
    }
    /**
     * 필요한 컬럼만 추출해서 테이블로 반환하는 함수 - rejectColumns 이용
     * @param t      복사할 테이블
     * @param colNms 복사할 컬럼명
     * @return new table
     */
    public static Table oldCreateTableWithColumns(Table t, String... colNms) {
        if (t == null) throw new IllegalArgumentException("table is null");
        List<String> colNmList = Arrays.stream(colNms).collect(Collectors.toList());

        // 제거할 컬럼명 만들기
        List<String> removeColNms = new ArrayList<>();
        t.columnNames().forEach(colNm -> {
            if (!colNmList.contains(colNm)) {
                removeColNms.add(colNm);
            }
        });
        return t.rejectColumns(removeColNms.toArray(new String[0]));
    }
}
