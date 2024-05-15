package org.study.green.tablesaw;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.selection.Selection;

public class TableSawStart {

    private DoubleColumn nc;

    public TableSawStart() {
        System.out.println("TableSawStart.TableSawStart");
        DoubleColumn nc = ColumnsTest();
        this.setNc(nc);
        DoubleColumn nc2 = ArrayOperationsTest(nc);
        SelectionsTest(nc);
        SelectingbyIndex(nc);
        Mapfunctions(nc, nc2);
        ReduceFunctions();
        System.out.println("TableSawStart.TableSawStart - End");
    }

    /**
     * Reduce (aggregate) functions: Summarizing a column
     * 어떤 의미에서 열의 데이터를 요약하는 단일 값을 파생하려는 경우가 있습니다.
     * 집계 함수가 바로 그런 일을 합니다.
     * 이러한 각 함수는 열의 모든 값을 검색하고 결과로 단일 스칼라 값을 반환합니다.
     * 모든 열은 min () 및 max ()와 같은 일부 집계 함수를 지원합니다( 예: count() , countUnique() 및 countMissing() ) .
     * 일부는 유형별 기능도 지원합니다.
     * 예를 들어 BooleanColumn은 열의 모든 값이 true 인 경우 true를 반환하는 all()을 지원합니다 .
     * any() 및 none() 함수는 각각 열의 값이 true이거나 true인 경우 true를 반환합니다.
     * countTrue() 및 countFalse() 함수 도 사용할 수 있습니다.
     *
     * NumberColumn에는 더 많은 집계 함수가 있습니다.
     * 예를 들어 열 값의 표준 편차를 계산하려면 다음을 호출합니다.
     */
    private void ReduceFunctions() {
        System.out.println("TableSawStart.ReduceFunctions");
        double stdDev = nc.standardDeviation();
        double sum = nc.sum();
        System.out.println("sum = " + sum);
        System.out.println("stdDev = " + stdDev);

        double mean = nc.mean();
        System.out.println("mean = " + mean);
        double range = nc.range();
        System.out.println("range = " + range);
        System.out.println("TableSawStart.ReduceFunctions - End");
    }

    /**
     * Map functions
     * 이미 본 적이 있습니다.
     * 맵 함수는 새 열을 결과로 반환하는 열에 정의된 메서드입니다.
     * 위의 열 Multiply(aNumber) 메서드는 스칼라 인수가 있는 맵 함수입니다.
     * 두 열의 값을 곱하려면 Multiply(aNumberColumn)를 사용하세요 .
     *
     * @param nc
     * @param nc2
     */
    private void Mapfunctions(DoubleColumn nc, DoubleColumn nc2) {
        System.out.println("TableSawStart.Mapfunctions");

        //        DoubleColumn newColumn = nc1.multiply(nc2);

        DoubleColumn other = DoubleColumn.create("other", new Double[]{10.0, 20.0, 30.0, 40.0});
        DoubleColumn newColumn = nc2.multiply(other);
        System.out.println(newColumn.print());
        /*
        이전 예제의 스칼라 값 대신 nc1 열의 각 값에 nc2의 해당 값을 곱합니다.
        다양한 열 유형에 대해 많은 맵 함수가 내장되어 있습니다. 다음은 StringColumn에 대한 몇 가지 예입니다.
         */

        StringColumn s = StringColumn.create("sc", new String[]{"foo", "bar", "baz", "foobarbaz"});
        StringColumn s2 = s.copy();
        System.out.println("s2.print() = " + s2.print());
        s2 = s2.replaceFirst("foo", "bar");
        System.out.println("s2.print() = " + s2.print());
        s2 = s2.upperCase();
        System.out.println("s2.print() = " + s2.print());
        s2 = s2.padEnd(5, 'x'); // put 4 x chars at the end of each string
        System.out.println("s2.print() = " + s2.print());
        s2 = s2.substring(1, 5);
        System.out.println("s2.print() = " + s2.print());

        // this returns a measure of the similarity (levenshtein distance) between two columns
        DoubleColumn distance = s.distance(s2);
        System.out.println("distance.print() = " + distance.print());

        /*
        보시다시피, 새로운 문자열을 반환하는 많은 String 메소드에 대해.
         StringColumn은 새 StringColumn을 반환하는 동등한 맵 메서드를 제공합니다.
         또한 Guava의 문자열 라이브러리와 Apache Commons 문자열 라이브러리에 있는 기타 유용한 방법도 포함되어 있습니다.
         */
        System.out.println("TableSawStart.Mapfunctions - End");
    }

    /**
     * Selecting by index
     * 다음 예에서는 조건자를 사용하여 선택하는 방법을 보여줍니다.
     * 선택 항목을 사용하여 특정 인덱스에서 값을 검색할 수도 있습니다.
     * 다음 두 가지가 모두 지원됩니다.
     *
     * @param nc
     */
    private void SelectingbyIndex(DoubleColumn nc) {
        System.out.println("TableSawStart.SelectingbyIndex");
        DoubleColumn selectingbyIndex01 = nc.where(Selection.with(0, 2));// returns 2 rows with the given indexes
        System.out.println("selectingbyIndex01.print() = " + selectingbyIndex01.print());
        DoubleColumn selectingbyIndex02 = nc.where(Selection.withRange(1, 3)); // returns rows 1 inclusive to 3 exclusive
        System.out.println("selectingbyIndex02.print() = " + selectingbyIndex02.print());
        // 데이터 테이블과 동일한 길이의 열이 여러 개 있는 경우 한 열을 선택하고 이를 사용하여 다른 열을 필터링할 수 있습니다.
        StringColumn sc = StringColumn.create("sc", "foo", "bar", "baz", "foobar");

        System.out.println("sc.print() = " + sc.print());
        Selection foo = sc.startsWith("foo");
        StringColumn where = sc.where(foo);
        System.out.println("where.print() = " + where.print());

        DoubleColumn result = nc.where(foo);
        System.out.println("result.print() = " + result.print());


        System.out.println("TableSawStart.SelectingbyIndex - End");

    }

    /**
     * Selections
     * 테이블로 넘어가기 전에 선택 사항 에 대해 이야기해야 합니다.
     * 선택 항목은 테이블과 열을 모두 필터링하는 데 사용됩니다.
     * 종종 백그라운드에서 작동하지만 직접 사용할 수도 있습니다.
     * 예를 들어 DoubleColumn{1, 2, 3, 4} 값이 포함되어 있다고 가정해 보겠습니다 .
     * 메시지를 보내 해당 열을 필터링할 수 있습니다. 예를 들어:
     * <p>
     * 이 작업은 Selection 을 반환합니다 .
     * 논리적으로 이는 원래 열과 동일한 크기의 비트맵입니다.
     * 위의 메서드는 사실상 1, 1, 0, 0을 반환합니다.
     * 열의 처음 두 값은 3보다 작고 마지막 두 값은 그렇지 않기 때문입니다.
     * <p>
     * 아마도 여러분이 원했던 것은 객체가 아니라 필터를 통과한 값만 포함하는 Selection새 객체였을 것 입니다.
     * DoubleColumn이를 얻으려면 where(aSelection) 메소드를 사용하여 선택을 적용합니다.
     *
     * @param nc
     */
    private void SelectionsTest(DoubleColumn nc) {
        System.out.println("TableSawStart.SelectionsTest");
        Selection lessThan = nc.isLessThan(3);
        for (Integer i : lessThan) {
            System.out.println("i = " + i);
        }

        System.out.println("lessThan = " + lessThan);

        DoubleColumn filtered = nc.where(lessThan);

        System.out.println(nc.get(1).equals(filtered.get(1)));
        System.out.println(nc.get(1).hashCode());
        System.out.println(filtered.get(1).hashCode());

        int nc3 = System.identityHashCode(nc.get(1));
        System.out.println("nc1 = " + nc3);
        int nc4 = System.identityHashCode(filtered.get(1));
        System.out.println("nc2 = " + nc4);

        System.out.println("filtered.print() = " + filtered.print());

        DoubleColumn filteredPositive = nc.where(nc.isLessThan(3).and(nc.isPositive()));

        System.out.println("filteredPositive.print() = " + filteredPositive.print());
        System.out.println("and " + nc.where(nc.isLessThan(3).and(nc.isGreaterThan(1))).print());
        ;

        System.out.println("TableSawStart.SelectionsTest - End");
    }

    /**
     * Array Operations
     * Tablesaw를 사용하면 열 작업이 쉬워집니다.
     * 예를 들어, 표준 Java의 숫자에 대해 작동하는 작업은 Tablesaw의 숫자 열 에서 작동하는 경우가 많습니다.
     * 열의 모든 값에 4를 곱하려면 원래 열과 같은 새 열을 반환하는 곱하기() 메서드를 사용합니다.
     * <p>
     * 보시다시피 값은 원본 값의 4배입니다.
     * 새 열의 이름은 원래의 "Test"와 연산(*4)을 결합하여 만들어집니다. 를 사용하고 싶다면 변경할 수 있습니다 setName(aString).
     * <p>
     * Tablesaw에는 열 기반 작업이 너무 많아서 일반적으로 열이나 테이블을 처리하기 위해 for 루프를 작성하는 경우 뭔가 누락될 수 있습니다.
     */
    private DoubleColumn ArrayOperationsTest(DoubleColumn nc) {
        System.out.println("TableSawStart.ArrayOperationsTest");
        DoubleColumn nc2 = nc.multiply(4);
        String name = nc2.name();
        System.out.println("name = " + name);
        System.out.println(nc2.print());

        nc2.setName("test");
        String name1 = nc2.name();
        System.out.println("name1 = " + name1);


        int nc3 = System.identityHashCode(nc.get(1));
        System.out.println("nc1 = " + nc3);
        int nc4 = System.identityHashCode(nc2.get(1));
        System.out.println("nc2 = " + nc4);

        System.out.println(nc.get(1) == nc2.get(1));


        System.out.println("TableSawStart.ArrayOperationsTest - End");
        return nc2;
    }

    /**
     * Columns
     * 열은 명명된 1차원 데이터 모음입니다. 테이블의 일부일 수도 있고 아닐 수도 있습니다.
     * 열의 모든 데이터는 동일한 유형이어야 합니다.
     * Tablesaw는 Strings, floats, doubles, ints, shorts, longs, booleans, LocalDates, LocalTimes, Instants 및 LocalDateTimes에 대한 열을 지원합니다. 날짜 및 시간 열은 Java 8에 도입된 java.time 클래스와 유사합니다.
     * 열을 생성하려면 정적 create() 메서드 중 하나를 사용할 수 있습니다 .
     */
    private DoubleColumn ColumnsTest() {
        System.out.println("TableSawStart.ColumnsTest");
        double[] numbers = {1, 2, 3, 4};
        String columnName = "nc";
        DoubleColumn nc = DoubleColumn.create(columnName, numbers);
        //        System.out.println("nc = " + nc);
        //        for (Double v : nc) {
        //            System.out.println("v = " + v.intValue());
        //            System.out.println("v = " + v.isNaN());
        //        }
        String print = nc.print();
        System.out.println("print = " + print);
        double three = nc.get(2);
        System.out.println("three = " + three);
        System.out.println("TableSawStart.ColumnsTest - End");
        return nc;
    }

    public static void main(String[] args) {
        System.out.println("TableSawStart.main");
        TableSawStart tableSawStart = new TableSawStart();
        //        System.out.println("nc.print() = " + tableSawStart.getNc().print());
        System.out.println("TableSawStart.main - End");
    }

    public void setNc(DoubleColumn nc) {
        this.nc = nc;
    }

    public DoubleColumn getNc() {
        return nc;
    }
}
