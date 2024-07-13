package org.study.green.utils;

import lombok.extern.slf4j.Slf4j;
import tech.tablesaw.api.Table;

import java.io.File;

@Slf4j
public class TableSawUtil {

    public static void showTableSawInfo(Table table) {
        log.info("\ntable name = {}", table.name());
        log.info("\ntable data = {}", table.print());
        log.info("\ntable rows = {} x cols = {} ", table.rowCount(), table.columnCount());
    }
}
