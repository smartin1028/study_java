package org.study.green.utils;

import java.io.File;

public class FileUtil {

    public static String convertFilePath(String filePath) {
        return filePath.replaceAll("/", File.separator).replaceAll("\\\\", File.separator);
    }

    public static String appendPath(String... pathNames) {
        return String.join(File.separator, pathNames);
    }
}
