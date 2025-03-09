package com.tree.tao.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * 암복호화 Util
 */
public class MyEncryptDecrypt {

    private static final String ALGORITHM = KeyInfo.ALGORITHM;
    private static final byte[] KEY = KeyInfo.KEY_VALUE.getBytes();

    /**
     * 암호화
     * @param  str    암호화할 문자열
     * @return String 암호화 문자열
     * @throws Exception
     */
    public static String encrypt(String str) throws Exception {
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(str.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 복호화
     * @param str       암호화 된 문자열
     * @return String   복호화 된 문자열
     * @throws Exception
     */
    public static String decrypt(String str) throws Exception {
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(str));
        return new String(decryptedBytes);
    }

}
