package org.tao.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    /**
     * null 과 공백 정상 수행 확인
     */
    @Test
    public void isNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(""));
        assertFalse(StringUtils.isNullOrEmpty("1"));
        assertFalse(StringUtils.isNullOrEmpty("asd"));
        assertFalse(StringUtils.isNullOrEmpty("null"));
    }

    /**
     * 공백, null일 경우 default메세지
     * 값이 있으면 현재 메시지
     */
    @Test
    public void defaultIfNullOrEmpty() {
        assertEquals("default value" , StringUtils.defaultIfNullOrEmpty(null, "default value"));
        assertEquals("default value" , StringUtils.defaultIfNullOrEmpty("", "default value"));
        assertEquals("default"       , StringUtils.defaultIfNullOrEmpty("default", "default value"));
        assertNotEquals("N"        , StringUtils.defaultIfNullOrEmpty("", "Y"));
    }

    /**
     * 기본값은 null일수 없습니다.
     */
    @Test(expected = NullPointerException.class)
    public void nullPointerException() {
        StringUtils.defaultIfNullOrEmpty("", null);
    }

}