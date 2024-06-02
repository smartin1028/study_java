package org.study.green.str;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StrUtil {

    public static String fillZeroString(String text, int size, boolean alignRight) {
        return String.format("%" + (alignRight ? "-" : "") + size + "s", text).replaceAll(" ", "0");
    }

    public static String fillZeroString(String text, int size) {
        return StrUtil.fillZeroString(text, size, false);
    }

    public static @NotNull String getString(Dotenv dotenv, String paramNm) {
        return Objects.requireNonNull(dotenv.get(paramNm)).replaceAll("'","");
    }
}
