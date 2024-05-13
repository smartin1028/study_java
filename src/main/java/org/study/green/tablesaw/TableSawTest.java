package org.study.green.tablesaw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableSawTest {
     Logger logger = LoggerFactory.getLogger(getClass());

    public TableSawTest() {
        double[] numbers = {1, 2, 3, 4};
        DoubleColumn nc = DoubleColumn.create("nc", numbers);
        logger.debug("{}", nc.print());
        double three = nc.get(2);
        DoubleColumn nc2 = nc.multiply(4);
        logger.debug("{}", nc2.print());

        StringColumn sc = StringColumn.create("sc", "foo", "bar", "baz", "foobar");
        DoubleColumn result = nc.where(sc.startsWith("foo"));
        logger.debug("{}", "result = " + result);

        DoubleColumn other = DoubleColumn.create("other", new Double[]{10.0, 20.0, 30.0, 40.0});
        DoubleColumn newColumn = nc2.multiply(other);
        logger.debug("{}", newColumn.print());

        String[] animals = {"bear", "cat", "giraffe"};
        double[] cuteness = {90.1, 84.3, 99.7};

        Table cuteAnimals =
                Table.create("Cute Animals")
                        .addColumns(
                                StringColumn.create("Animal types", animals),
                                DoubleColumn.create("rating", cuteness));

        logger.debug("{}", cuteAnimals.structure());

    }

    public static void main(String[] args) {
        TableSawTest tableSawTest = new TableSawTest();
    }
}
