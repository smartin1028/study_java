package org.study.green.str;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class MyEncryptDecrypt {

    private static final String ALGORITHM = KeyInfo.ALGORITHM;
    private static final byte[] KEY = KeyInfo.KEY_VALUE.getBytes();

    public static String encrypt(String data) throws Exception {
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String data) throws Exception {
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("KEY = " + KEY.length);

        String text = "암호화 테스트";
        String encryptedText = encrypt(text);
        String decryptedText = decrypt(encryptedText);

        System.out.println("원본 데이터: " + text);
        System.out.println("암호화된 데이터: " + encryptedText);
        System.out.println("복호화된 데이터: " + decryptedText);
    }
}
