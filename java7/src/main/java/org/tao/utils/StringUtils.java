package org.tao.utils;

import java.util.Objects;

public class StringUtils {

    /**
     * null 또는 공백 체크
     * @param str : null 또는 문자열
     * @return 널 or 공백 여부
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * null 또는 공백일 경우 기본값 반환
     * @param str           : 확인할 문자열
     * @param defaultValue  : 기본 문자열
     * @return str 널 or 공백인 경우 defaultValue 리턴함
     */
    public static String defaultIfNullOrEmpty(String str, String defaultValue) {
        if(defaultValue == null){
            Objects.requireNonNull(defaultValue, "defaultValue must not be null");
        }
        return isNullOrEmpty(str) ? defaultValue : str;
    }
}

