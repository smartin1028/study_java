package org.study.green.tablesaw;

import org.study.green.resource.ResourceUtils;
import org.study.green.utils.FileUtil;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;

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

        WorkingColumns01();
        WorkingColumns();

//        GettingSpecificColumnTypesFromTable();

        WorkingRowsTest();
        SortingTest();

        System.out.println("TableSawStartTable.TableSawStartTable - End");
    }

    /**
     * Sorting
     * 테이블을 정렬하려면 sortOn()메서드를 사용하고 하나 이상의 열 이름을 지정하면 됩니다.
     */
    private void SortingTest() {
        Table sorted = bushTable.sortOn("date", "-approval"); // Sorts Ascending by Default
//        sorted = bushTable.sortDescendingOn( "approval"); // Sorts Ascending by Default
        System.out.println("sorted = " + sorted.printAll());

//        sorted = table.sortAscendingOn("bar"); // just like sortOn(), but makes the order explicit.
//        sorted = table.sortDescendingOn("foo");
//
//        // sort on foo ascending, then bar descending. Note the minus sign preceding the name of
//        // column bar.
//        sorted = table.sortOn("foo", "-bar");

    }

    /**
     * Working with rows
     * 열과 마찬가지로 행 방식으로 테이블을 작업하기 위한 다양한 옵션이 있습니다. 다음은 몇 가지 유용한 것입니다:
     * 테이블의 각 행에 대해 임의의 작업을 수행할 수도 있습니다. 한 가지 방법은 행을 반복하고 각 열을 개별적으로 작업하는 것입니다.
     */
    private void WorkingRowsTest() {

        bushTable.sortAscendingOn("index");
        Table result = bushTable.dropDuplicateRows();

        System.out.println("result.rowCount() = " + result.rowCount());
        result = bushTable.dropRowsWithMissingValues();

        System.out.println("result.rowCount() = " + result.rowCount());
        // drop rows using Selections
        result = bushTable.dropWhere(bushTable.numberColumn(1).isLessThan(60));
        System.out.println("result.rowCount() = " + result.rowCount());
        // add rows
        bushTable.addRow(43, bushTable); // adds row 43 from sourceTable to the receiver
        System.out.println("bushTable.rowCount() = " + bushTable.rowCount());
        // sampling
        Table rows = bushTable.sampleN(200);// select 200 rows at random from table
        System.out.println("rows.print() = " + rows.print());
        System.out.println("result.rowCount() = " + rows.rowCount());


        for (Row row : bushTable) {
//            System.out.println("row.getDate(0) = " +row.getDate(0) + "row.getString(1) = " + row.getInt(1));
        }

        bushTable.stream()
                .forEach(
                        row -> {
                            System.out.println("row.getDate(0) = " + row.getDate(0) + "row.getString(1) = " + row.getInt(1));
                        }
                );

        // sampleN로 가져온 값중 가장 큰값 가져오기
        OptionalDouble max = rows.stream().mapToDouble(row -> row.getInt(1)).max();
        System.out.println("max = " + max);


        Consumer<Row[]> consumer = rows1 -> {
            System.out.println("====================================================");
            OptionalDouble
                    max1 = Arrays
                            .stream(rows1)
                            .mapToDouble(row -> {
                                System.out.println("row = " + row);
                                return row.getInt(1);
                            }).max();
            
            System.out.println("max1 = " + max1);
            System.out.println("rows1 = " + rows1.length);
        };


        //        // Consumer prints out the max of a window.
//        Consumer<Row[]> consumer =
//                ()->{ rows -> System.out.println(Arrays.stream(rows).mapToDouble(row -> row.getDouble(1)).max())};
//
        // Streams over rolling sets of rows. I.e. 0 to n-1, 1 to n, 2 to n+1, etc.
        // 3개의 범위
//        bushTable.rollingStream(bushTable.rowCount()).forEach(consumer);
        bushTable.rollingStream(3).forEach(consumer);
        System.out.println("====================================================");
        
        // Streams over stepped sets of rows. I.e. 0 to n-1, n to 2n-1, 2n to 3n-1, etc. Only returns
        // full sets of rows.
//        bushTable.steppingStream(5).forEach(consumer);
//        System.out.println("bushTable.printAll() = " + bushTable.printAll());
    }

    /**
     * Getting specific column types from a table
     * 이름이나 위치를 기준으로 테이블에서 열을 검색할 수 있습니다. 가장 간단한 메소드인 column()은 Column 유형의 객체를 반환합니다.
     * 이것만으로도 충분할 수 있지만 특정 유형의 열을 가져오고 싶은 경우가 많습니다.
     * 예를 들어, 분산형 차트에서 해당 값을 사용하려면 반환된 값을 NumberColumn으로 캐스팅해야 합니다.
     */
    private void GettingSpecificColumnTypesFromTable() {
//        bushTable.column("date"); // returns the column named 'date' if it's in the table.
//        // or
//        bushTable.column(0); // returns the first column
//
//        DateColumn dc = (DateColumn) bushTable.column(0);
//
//        System.out.println("dc = " + dc);
//
    }

    private void WorkingColumns() {

        int i = bushTable.rowCount();
        String[] animals = {"bear", "cat", "giraffe"};

        String[] adds = new String[i];

        for (int j = 0; j < animals.length; j++) {
            adds[j] = animals[j];
        }

        bushTable.addColumns(StringColumn.create("Animal types", adds));
        List<String> columnNames = bushTable.columnNames(); // returns all column names
        List<Column<?>> columns = bushTable.columns(); // returns all the columns in the table


        System.out.println(bushTable.printAll());
        for (String columnName : columnNames) {
            System.out.println("columnName = " + columnName);
        }
        System.out.println("====================================================");

        // 해당 컬럼 삭제
//        bushTable.removeColumns("who"); // keep everything but "foo"
        // 해당 컬럼만 남기고 삭제
//        bushTable.retainColumns("who", "date"); // only keep who and date
        // 비어있는 값이 있는 컬럼은 삭제함
        bushTable.removeColumnsWithMissingValues();

        columnNames = bushTable.columnNames(); // returns all column names

        for (String columnName : columnNames) {
            System.out.println("columnName = " + columnName);
        }

        System.out.println("====================================================");
        System.out.println("columns.size() = " + columns.size());

        System.out.println("columns.get(0).copy().asList() = " + columns.get(0).copy().asList());
        // adding columns


        // 데이터 정렬
        columns.get(0).sortAscending();

        Column<?> objects = columns.get(0);

        StringColumn stringColumn = objects.asStringColumn();
        stringColumn.setName("date2");
        String s = stringColumn.get(stringColumn.size() - 1);
        s = "test";
        bushTable.addColumns(stringColumn);
        // 컬럼이름은 중복하면 오류남
        // copy해서 동일 컬럼 생성 가능
        bushTable.addColumns(columns.get(0).copy().setName("test3"));
        System.out.println("bushTable.printAll() = " + bushTable.first(5));
    }

    /**
     * Working with a table’s columns
     */
    private void WorkingColumns01() {

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

        System.out.println("columns = " + bushTable.column("wHo") + columns);
        System.out.println("columns = " + bushTable.column("whO") + columns);
        System.out.println("columns = " + bushTable.column("Who") + columns);

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


        cuteAnimals.write().csv(FileUtil.appendPath(ResourceUtils.getResourcePath() , "bush.csv"));

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
