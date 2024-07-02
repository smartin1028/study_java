package org.study.green.tablesaw;

import org.study.green.resource.ResourceUtils;
import org.study.green.utils.DateUtils;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

public class TableSawGroupBy {

    public static void main(String[] args) {
         // CSV 파일로부터 데이터프레임을 생성합니다.
        Table df = Table.read().csv(ResourceUtils.getResourcePath() + "/bush1.csv");

        df.addColumns(StringColumn.create("date2", df.rowCount()));

        df.forEach(row ->{
            String s = DateUtils.convertLocalDateToStr(row.getDate("date"), "yyyyMM");
            row.setString("date2", s);
        });

        Table by = df.summarize("approval", sum, mean, count).by("who" , "date2");
        String[] strings = {"Count [approval]", "who", "date2", "Sum [approval]", "Mean [approval]"};
        List<String> list = Arrays.asList(strings);
        Collections.sort(list);
        String[] array = list.toArray(new String[0]);
        by.retainColumns(array);
        System.out.println("by = " + by.print());

    }
}
