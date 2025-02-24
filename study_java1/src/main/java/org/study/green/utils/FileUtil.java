package org.study.green.utils;

import java.io.File;

public class FileUtil {

    public static final String SEPARATOR = File.separator;

    public static String convertFilePath(String filePath) {
        String separator = SEPARATOR;
        separator += separator;
        String s = filePath.replaceAll("/", separator);
        return s.replaceAll("\\\\", separator);
    }

    public static String appendPath(String... pathNames) {
        return String.join(SEPARATOR, pathNames);
    }


}
