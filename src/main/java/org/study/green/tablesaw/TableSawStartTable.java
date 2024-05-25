package org.study.green.tablesaw;

import org.study.green.resource.ResourceUtils;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class TableSawStartTable {

    private DoubleColumn nc;
    private Table cuteAnimals;
    private Table bushTable;

    public TableSawStartTable() {
        System.out.println("TableSawStartTable.TableSawStartTable");
        double[] numbers = {1, 2, 3, 4};
        String columnName = "nc";
        nc = DoubleColumn.create(columnName, numbers);
        CreatingTables();
        ImportingData();
        ExploringTables();

        WorkingColumns();

        GettingSpecificColumnTypesFromTable();

        System.out.println("TableSawStartTable.TableSawStartTable - End");
    }

    /**
     * Getting specific column types from a table
     * 이름이나 위치를 기준으로 테이블에서 열을 검색할 수 있습니다. 가장 간단한 메소드인 column()은 Column 유형의 객체를 반환합니다.
     * 이것만으로도 충분할 수 있지만 특정 유형의 열을 가져오고 싶은 경우가 많습니다.
     * 예를 들어, 분산형 차트에서 해당 값을 사용하려면 반환된 값을 NumberColumn으로 캐스팅해야 합니다.
     */
    private void GettingSpecificColumnTypesFromTable() {
        bushTable.column("date"); // returns the column named 'date' if it's in the table.
        // or
        bushTable.column(0); // returns the first column
        
        DateColumn dc = (DateColumn) bushTable.column(0);

        System.out.println("dc = " + dc);
        
    }

    /**
     * Working with a table’s columns
     */
    private void WorkingColumns() {

        List<String> strings = bushTable.columnNames();
        for (String string : strings) {
            System.out.println("string = " + string);
        }

        List<Column<?>> columns = bushTable.columns(); // returns all the columns in the table
        for (Column<?> column : columns) {

        }


        // removing columns
//        bushTable.removeColumns("who"); // keep everything but "who"
//        bushTable.retainColumns("who"/*, "Bar"*/); // only keep foo and bar
//        bushTable.removeColumnsWithMissingValues();


        strings = bushTable.columnNames();
        for (String string : strings) {
            System.out.println("string = " + string);
        }
        // Tablesaw에서 열 이름은 대소문자를 구분하지 않습니다. 다음 중 하나를 요청하면 동일한 열을 얻게 됩니다.

        System.out.println("columns = " + bushTable.column("wHo")+ columns);
        System.out.println("columns = " + bushTable.column("whO")+ columns);
        System.out.println("columns = " + bushTable.column("Who")+ columns);

        bushTable.stream().forEach(x -> {
            List<String> strings1 = x.columnNames();
            for (String s : strings1) {
//                System.out.println("key = " + s);
//                System.out.println("value = " + x.getObject(s));
            }
//            System.out.println(x.getString("who"));
        });

        System.out.println("bushTable.stream().count() = " + bushTable.stream().count());

        // 종종 테이블의 특정 열을 사용하여 작업하게 됩니다. 다음은 몇 가지 유용한 방법입니다.
        // adding columns
//        bushTable.addColumns(column1, column2, column3);

//        List<String> columnNames = table.columnNames(); // returns all column names
//        List<Column<?>> columns = table.columns(); // returns all the columns in the table
//
//        // removing columns
//        table.removeColumns("Foo"); // keep everything but "foo"
//        table.retainColumns("Foo", "Bar"); // only keep foo and bar
//        table.removeColumnsWithMissingValues();
//
//        // adding columns
//        table.addColumns(column1, column2, column3);
    }

    private void ExploringTables() {
        System.out.println("TableSawStartTable.ExploringTables");
        System.out.println("cuteAnimals.structure() = " + bushTable.structure());
        System.out.println("cuteAnimals.structure() = " + bushTable.shape());
        System.out.println("cuteAnimals.structure() = " + bushTable.first(2));
        System.out.println("cuteAnimals.structure() = " + bushTable.last(2));

        System.out.println("TableSawStartTable.ExploringTables - End");
    }

    /**
     * Importing data
     * 더 자주, CSV 또는 기타 구분된 텍스트 파일에서 테이블을 로드하게 됩니다.
     */
    private void ImportingData() {
        System.out.println("TableSawStartTable.ImportingData");
        // https://jtablesaw.github.io/tablesaw/userguide/importing_data
        bushTable = Table.read().csv(ResourceUtils.getClassResourcePath() + "/bush1.csv");
        System.out.println("bushTable.print() = " + bushTable.print());
        System.out.println("TableSawStartTable.ImportingData - End");
    }

    /**
     * 테이블은 이름이 지정된 열 모음입니다.
     * 테이블의 모든 열에는 동일한 수의 요소가 있어야 하지만 누락된 값은 허용됩니다.
     * 테이블에는 열 유형의 모든 조합이 포함될 수 있습니다.
     * <p>
     * 테이블 생성
     * 코드에서 테이블을 만들 수 있습니다.
     * 여기서는 테이블을 만들고 여기에 두 개의 새 열을 추가합니다.
     */
    private void CreatingTables() {

        String[] animals = {"bear", "cat", "giraffe"};
        double[] cuteness = {90.1, 84.3, 99.7};

        cuteAnimals = Table.create("Cute Animals")
                .addColumns(
                        StringColumn.create("Animal types", animals),
                        DoubleColumn.create("rating", cuteness));

        System.out.println(cuteAnimals.structure());
        System.out.println(cuteAnimals.shape());
        System.out.println(cuteAnimals.first(1));


        cuteAnimals.write().csv(ResourceUtils.getResourcePath() + "bush.csv");

        File file = new File("file.txt");
        String absolutePath = file.getAbsolutePath();
        System.out.println("Absolute Path: " + absolutePath);
    }


    public static void main(String[] args) {
        System.out.println("TableSawStartTable.main");
        TableSawStartTable tableSawStart = new TableSawStartTable();
        //        System.out.println("nc.print() = " + tableSawStart.getNc().print());
        System.out.println("TableSawStartTable.main - End");
    }

    public void setNc(DoubleColumn nc) {
        this.nc = nc;
    }

    public DoubleColumn getNc() {
        return nc;
    }
}
