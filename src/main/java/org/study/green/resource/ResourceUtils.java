package org.study.green.resource;

import java.io.IOException;

public class ResourceUtils {

    /**
     * /~.../study_java/target/classes/
     *
     * @return
     */
    public static String getClassResourcePath() {
        return ResourceUtils.class.getClassLoader().getResource("./").getPath();
    }

    public static String getRootPath() {
        return System.getProperty("user.dir");
    }

    public static String getResourcePath() {
        return String.format("%s/%s", getRootPath(), "src/main/resources/");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("resourcePath = " + ResourceUtils.getClassResourcePath());
        System.out.println("project root path = " + ResourceUtils.getRootPath());
    }
}
