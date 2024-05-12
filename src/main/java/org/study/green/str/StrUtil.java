package org.study.green.str;

public class StrUtil {

    public static String fillZeroString(String text, int size, boolean alignRight){
        return String.format("%"+ (alignRight ? "-" : "") +size +"s", text).replaceAll(" ", "0");
    }

    public static String fillZeroString(String text, int size){
        return StrUtil.fillZeroString(text, size, false);
    }
}
