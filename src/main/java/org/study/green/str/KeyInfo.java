package org.study.green.str;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class KeyInfo {
    public static final String ALGORITHM = "AES";
    public static final String KEY_VALUE = "mOFdVsmw9/U5erMgDONYlQ==";
    private static final int AES_KEY_SIZE = 16;
    
    public static byte[] generateAESKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[AES_KEY_SIZE];
        random.nextBytes(key);
        return key;
    }

    public static void main(String[] args) {
        byte[] key = generateAESKey();
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated AES Key (Base64 Encoded): " + encodedKey);
        System.out.println("Generated AES Key (Base64 Encoded): " + encodedKey.length());
    }
}
