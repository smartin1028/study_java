package org.study.green.tablesaw;

import org.junit.Test;
import org.study.green.resource.ResourceUtils;
import org.study.green.utils.DateUtils;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.aggregate.AggregateFunctions.count;

public class TableSawGroupByTest {
    public static final String BUSH_1_CSV = ResourceUtils.getResourcePath() + "/bush1.csv";

    @Test
    public void t_DateUtilsTest_this_00() {
        // CSV 파일로부터 데이터프레임을 생성합니다.
        Table df = Table.read().csv(BUSH_1_CSV);

        Table structure = df.structure();
        System.out.println("structure = " + structure);

        df.addColumns(df.column("date").asStringColumn().copy().setName("date2"));
        df.forEach(row ->{
            LocalDate date = row.getDate("date");
            row.setString("date2",
                    DateUtils.convertLocalDateToStr(date, "yyyyMM")
            );
        });

        Table by = df.summarize("approval", sum, mean, count).by("who" , "date2");
        System.out.println("by = " + by.print());

    }

    @Test
    public void t_DateUtilsTest_this_02() {

        // CSV 파일로부터 데이터프레임을 생성합니다.
        Table df = Table.read().csv(BUSH_1_CSV);

        Table structure = df.structure();
        System.out.println("structure = " + structure);

        // when
        df.addColumns(df.column("date").asStringColumn().copy().setName("date2"));

        String strCol = "str_date";
        StringColumn emptyCol = StringColumn.create(strCol, Collections.nCopies(df.rowCount(), null));
        df.addColumns(emptyCol);

        df.forEach(row ->{
            LocalDate date = row.getDate("date");
            row.setString("date2",
                    DateUtils.convertLocalDateToStr(date, "yyyyMM")
            );

            row.setString(strCol,
                    DateUtils.convertLocalDateToStr(date, "yyyyMM")
            );
        });

//        df.removeColumns("index", "date");

        Set<String> ss = new HashSet<>();
        // 컬럼이 없어도 에러
//        ss.add("test");
        ss.add("date");
        ss.add("index");

        String[] array = ss.toArray(new String[0]);
        // 사용할 컬럼만 남김
//        df.retainColumns(array);

        System.out.println("df.structure() = " + df.structure());

        System.out.println("df.print() = " + df.print());

        Table by = df.summarize("approval", sum, mean, count).by("who" , "date2");
        System.out.println("by = " + by.print());

    }

    /**
     * 일반적인 컬럼 변경
     *
     * LOCAL_DATE -> STRING
     */
    @Test
    public void t_column_copy_03() {
        Table table = Table.read().csv(BUSH_1_CSV);
        DateColumn date = table.dateColumn("date");
        StringColumn sc = date.asStringColumn().setName("strDate");
        table.addColumns(sc);
        System.out.println("table = " + table.structure());

        StringColumn emptyColumn = StringColumn.create("empty_col", Collections.nCopies(table.rowCount(), ""));


        StringColumn emptyCol02 = StringColumn.create("empty_col_02", table.rowCount());
        System.out.println("emptyCol02 = " + emptyCol02);


        table.addColumns(emptyCol02);
        System.out.println("table = " + table.structure());

        System.out.println("table.print() = " + table.print());


        emptyColumn.setName("addEmptyCol");

        table.addColumns(emptyColumn);
        System.out.println("table = " + table.structure());


        List<String> strings = Collections.nCopies(table.rowCount(), null);
        System.out.println("strings.size() = " + strings.size());

    }

}