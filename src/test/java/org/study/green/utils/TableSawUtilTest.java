package org.study.green.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.study.green.resource.ResourceUtils;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.DateAggregateFunction;
import tech.tablesaw.aggregate.StringAggregateFunction;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.study.green.utils.TableSawUtil.showTableSawInfo;
import static tech.tablesaw.api.ColumnType.*;

@Slf4j
public class TableSawUtilTest {
    private Table table;

    @Before
    public void setUp() throws Exception {
        ColumnType[] columnTypes = {LOCAL_DATE, INTEGER, STRING};
        table = readBush(columnTypes);
        String[] arr = {"date", "cnt", "who"};
        for (int i = 0; i < arr.length; i++) {
            table.column(i).setName(arr[i]);
        }
    }

    static Table readBush(ColumnType[] columnTypes) {
        String fileName = FileUtil.appendPath(ResourceUtils.getResourceTestPath(), "data", "data.csv");
        return Table.read()
                .csv(CsvReadOptions.builder(new File(fileName)).columnTypes(columnTypes));
    }

    @Test
    public void tShowTableSawInfo() {
        showTableSawInfo(table);
        assertTrue(TableSawUtil.hasColumnName(table, "date"));
        assertTrue(TableSawUtil.hasColumnName(table, "cnt"));
        assertTrue(TableSawUtil.hasColumnName(table, "who"));
        assertFalse(TableSawUtil.hasColumnName(table, "date1"));
        assertFalse(TableSawUtil.hasColumnName(null, "date1"));
        assertFalse(TableSawUtil.hasColumnName(table, null));
    }

    /**
     * rejectColumns, selectColumns 새로운 테이블 생성
     * retainColumns, removeColumns 현재 테이블 변경
     */
    @Test
    public void t_TableSawUtilTest_table_00() {
        // 새로 생성함

        Table tableWithColumns = TableSawUtil.createTableWithColumns(table, "cnt", "who");

        assertTrue(TableSawUtil.hasColumnName(tableWithColumns, "cnt"));
        assertTrue(TableSawUtil.hasColumnName(tableWithColumns, "who"));
        assertFalse(TableSawUtil.hasColumnName(tableWithColumns, "who1"));

        assertNotEquals(tableWithColumns, table);
        showTableSawInfo(tableWithColumns);

        Table colReject = table.rejectColumns("date");
        assertFalse(TableSawUtil.hasColumnName(colReject, "date"));

        assertNotEquals(colReject, table);

        Table colSelect = table.selectColumns("date");
        assertTrue(TableSawUtil.hasColumnName(colSelect, "date"));
        assertNotEquals(colSelect, table);

        // 새로 생성하지 않음
        Table retainColumns = table.retainColumns("date", "who");
        assertEquals(retainColumns, table);
        Table removeColumns = table.removeColumns("date");
        assertEquals(removeColumns, table);
    }

    @Test
    public void t_TableSawUtilTest_groupby_00() {
        Summarizer who = table.summarize("who", "date", new StringAggregateFunction("new_col_nm") {
            @Override
            public String summarize(StringColumn column) {


                log.info("summarize:{}", column.name());

//                log.info("summarize:{}", column.getString(1));
                return "";
            }
        });

        Table by = who.by("who", "cnt");
        by.removeColumns(by.columns().size() - 1);
        showTableSawInfo(by);
    }

    @Test
    public void t_TableSawUtilTestDropDuplicateRows() {
        Table selectedColNmsUnique = table.selectColumns("who").dropDuplicateRows();
        showTableSawInfo(selectedColNmsUnique);
    }

    @Test
    public void t_TableSawUtilTestGroupBy() {
        Summarizer who = table.summarize("date", new DateAggregateFunction("new_col_nm") {
            @Override
            public LocalDate summarize(DateColumn column) {
                System.out.println("column.size() = " + column.size());
                column.forEach(dateObj -> System.out.println("row = " + dateObj));

                return null;
            }
//
//            @Override
//            public String summarize(StringColumn column) {
//                // 새로 생성되는 컬럼의 값
//                System.out.println("column.size() = " + column.size());
//                return "";
//            }
        });

        Table by = who.by("who");
        by.removeColumns(by.columns().size() - 1);
        showTableSawInfo(by);
    }


    /**
     * group by
     *
     * DateColumn -> return String
     */
    @Test
    public void t_tableSawUtilTestGroupBy() {
        showTableSawInfo(table);
        Summarizer who = table.summarize("date", new AggregateFunction<DateColumn, String>("new_col_nm") {
            // 이곳으로 list 형식으로 넘어옴
            @Override
            public String summarize(DateColumn column) {
                List<String> list1 = column.asStringColumn().asList();
                List<String> collect = list1.stream().map(m -> m.substring(0, 1)).collect(Collectors.toList());
                System.out.println("list1.size() = " + collect.size());
                if (list1.size() < 200) {
                    return String.join("/", collect);
                }
                return "";
            }

            @Override
            public boolean isCompatibleColumn(ColumnType type) {
                return type.equals(LOCAL_DATE);
            }

            @Override
            public ColumnType returnType() {
                return STRING;
            }
        });

        Table by = who.by("who");
        showTableSawInfo(by);
    }

    /**
     * LocalDateTime group by max
     */
    @Test
    public void t_TableSawUtilTestGroupByMax() {
        showTableSawInfo(table);
        Summarizer who = table.summarize("date", new AggregateFunction<DateColumn, LocalDate>("new_col_nm") {

            @Override
            public LocalDate summarize(DateColumn column) {
                return column.max();
            }

            @Override
            public boolean isCompatibleColumn(ColumnType type) {
                return type.equals(LOCAL_DATE);
            }

            @Override
            public ColumnType returnType() {
                return LOCAL_DATE;
            }
        });

        Table by = who.by("who");
        showTableSawInfo(by);
    }


    /**
     * 선택한 값 변환
     */
    @Test
    public void t_TableSawUtilTestSetDataBySelection() {
        Selection selection = table.stringColumn("who").isEqualTo("fox");
        table.stringColumn("who").set(selection, "fox1");
        List<String> list = table.selectColumns("who").stringColumn("who").unique().asList();
        System.out.println("list = " + list);

    }
}