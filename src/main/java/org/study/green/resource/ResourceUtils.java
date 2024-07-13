package org.study.green.resource;

import org.study.green.utils.FileUtil;

public class ResourceUtils {

    /**
     * /~.../study_java/target/classes/
     */
    public static String getClassResourcePath() {
        return ResourceUtils.class.getClassLoader().getResource(FileUtil.convertFilePath("./")).getPath();
    }

    public static String getRootPath() {
        return System.getProperty("user.dir");
    }

    public static String getResourcePath() {
        return FileUtil.appendPath(getRootPath(), FileUtil.convertFilePath("src/main/resources"));
    }

    /**
     * test resources 위치 return
     * @return
     */
    public static String getResourceTestPath() {
        return FileUtil.appendPath( getRootPath(), FileUtil.convertFilePath("src/test/resources"));
    }

}
