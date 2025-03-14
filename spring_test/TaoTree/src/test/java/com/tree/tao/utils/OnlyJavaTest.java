package com.tree.tao.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class OnlyJavaTest {

    @Test
    public void t_OnlyJavaTest_true_00() {
        String jasyptPassword = System.getenv("JASYPT_PASSWORD");//
        System.out.println("jasyptPassword = " + jasyptPassword);
        boolean empty = StringUtils.isEmpty(jasyptPassword);
        System.out.println("empty = " + empty);

        // (isEmpty(str) ? defaultStr : str)
        String s = StringUtils.isEmpty(jasyptPassword) ? "This is an encryption key. It must never be exposed or made public." : "aa";
        System.out.println("s = " + s);

        String s1 = StringUtils.defaultIfEmpty(jasyptPassword, "This is an encryption key. It must never be exposed or made public.");
        System.out.println("s1 = " + s1);
    }

    @Test
    public void t_OnlyJavaTest_true_01() {
        Map<String, String> getenv = System.getenv();
        for (String s : getenv.keySet()) {
            System.out.println(s + " = " + getenv.get(s));
        }

    }

}
