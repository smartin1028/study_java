package org.study.green.tablesaw;

import org.study.green.resource.ResourceUtils;
import org.study.green.utils.FileUtil;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.File;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.QuerySupport.*;
public class TableSawStartTableWhere {

    private DoubleColumn nc;
    private Table cuteAnimals;
    private Table bushTable;

    public TableSawStartTableWhere() {
        System.out.println("TableSawStartTable.TableSawStartTable");
        double[] numbers = {1, 2, 3, 4};
        String columnName = "nc";
        nc = DoubleColumn.create(columnName, numbers);
        CreatingTables();
        ImportingData();

        FilteringTest();
        SummarizingTest();



        System.out.println("TableSawStartTable.TableSawStartTable - End");
    }

    /**
     * Summarizing 요약
     *
     */
    private void SummarizingTest() {
        
        Table summary = bushTable.summarize("approval", mean, sum, min, max).by("who");
//        Table summary = bushTable.summarize("approval", mean, sum, min, max).by("date", "who");
        System.out.println("summary.printAll() = " + summary.printAll());
    }

    /**
     * Filtering
     * 1. 쿼리 필터는 and , or , not 논리 연산을 사용하여 결합할 수 있습니다 . 이는 QuerySupport클래스에서 구현됩니다.
     *
     * 2. 각 메소드는 다음 서명이 있는 함수를 허용합니다 Function<Table, Selection>. Lambadas는 잘 작동합니다.
     */
    private void FilteringTest() {

        Table result =
                bushTable.where(
                        and(
                                or(
                                        t -> t.intColumn(1).isLessThan(55),
                                        t -> t.intColumn("approval").isLessThan(55)
//                                        t -> t.doubleColumn("nc1").isGreaterThan(4),
//                                        t -> t.doubleColumn("nc1").isNegative()
                                ),
                                not(
                                        t -> t.intColumn("index").isEqualTo(64)
                                )
                        )
                );
        System.out.println("result.rowCount() = " + result.rowCount());
        System.out.println("result.rowCount() = " + result.printAll());
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


        cuteAnimals.write().csv(FileUtil.appendPath(ResourceUtils.getResourcePath(), "bush.csv"));

        File file = new File("file.txt");
        String absolutePath = file.getAbsolutePath();
        System.out.println("Absolute Path: " + absolutePath);
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

    public static void main(String[] args) {
        System.out.println("TableSawStartTable.main");
        TableSawStartTableWhere tableSawStart = new TableSawStartTableWhere();
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
