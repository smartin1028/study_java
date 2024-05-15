package org.study.green.tablesaw;

import org.study.green.resource.ResourceUtils;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class TableSawStartTable {

    private DoubleColumn nc;

    public TableSawStartTable() {
        System.out.println("TableSawStartTable.TableSawStartTable");
        double[] numbers = {1, 2, 3, 4};
        String columnName = "nc";
        nc = DoubleColumn.create(columnName, numbers);
        CreatingTables();
        ImportingData();


        System.out.println("TableSawStartTable.TableSawStartTable - End");
    }

    /**
     * Importing data
     * 더 자주, CSV 또는 기타 구분된 텍스트 파일에서 테이블을 로드하게 됩니다.
     */
    private void ImportingData() {
        // https://jtablesaw.github.io/tablesaw/userguide/importing_data
//        Table bushTable = Table.read().csv("../data/bush.csv");
    }

    /**
     * 테이블은 이름이 지정된 열 모음입니다.
     * 테이블의 모든 열에는 동일한 수의 요소가 있어야 하지만 누락된 값은 허용됩니다.
     * 테이블에는 열 유형의 모든 조합이 포함될 수 있습니다.
     *
     * 테이블 생성
     * 코드에서 테이블을 만들 수 있습니다.
     * 여기서는 테이블을 만들고 여기에 두 개의 새 열을 추가합니다.
     *
     */
    private void CreatingTables() {

        String[] animals = {"bear", "cat", "giraffe"};
        double[] cuteness = {90.1, 84.3, 99.7};

        Table cuteAnimals =
                Table.create("Cute Animals")
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
