package org.study.green.utils;

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

public class KeyInfoTest {

    @Test
    public void mainTest() {
        byte[] key = KeyInfo.generateAESKey();
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated AES Key (Base64 Encoded): " + encodedKey);
        System.out.println("Generated AES Key (Base64 Encoded): " + encodedKey.length());

    }

}