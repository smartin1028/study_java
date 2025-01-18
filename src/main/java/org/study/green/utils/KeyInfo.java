package org.study.green.utils;

import java.security.SecureRandom;

/**
 * 암복호화 관련 객체
 */
public class KeyInfo {
    public static final String ALGORITHM = "AES";
    public static final String KEY_VALUE = "mOFdVsmw9/U5erMgDONYlQ==";
    private static final int AES_KEY_SIZE = 16;

    /**
     * 암호화 key 랜덤 생성
     * @return 생성된 암호화 key
     */
    public static byte[] generateAESKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[AES_KEY_SIZE];
        random.nextBytes(key);
        return key;
    }

}
