package org.study.green.utils;

import org.junit.Test;

import java.time.LocalDateTime;

public class DateUtilsTest{

    @Test
    public void t_DateUtilsTest_defaultStrDate_00() {
        String s = DateUtils.defaultStrDate();
        System.out.println("s = " + s);
    }

    @Test
    public void t_DateUtilsTest_defaultStrDateOfLocalDateTime_00() {
        String s = DateUtils.defaultStrDateOfLocalDateTime(LocalDateTime.now());
        System.out.println("s = " + s);
    }

    @Test
    public void t_DateUtilsTest_convertNowDateToStrByPattern_01() {
        String s = DateUtils.convertNowDateToStrByPattern("yyyyMMdd");
        System.out.println("s = " + s);
    }

}