package org.study.green.javautil;

import org.junit.Test;
import org.study.green.resource.ResourceUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class StringTest {

    @Test
    public void t_StringTest_true_00() {

//        String url = "한글";
//        String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
//        System.out.println(encoded);
//
//        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
//        System.out.println(decoded);

        String resourceTestPath = ResourceUtils.getResourceTestPath();
        System.out.println("resourceTestPath = " + resourceTestPath);

    }
}
