package org.study.green.str;


import org.junit.Test;
import org.study.green.utils.MyEncryptDecrypt;

public class MyEncryptDecryptTest {

    @Test
    public void My_01() throws Exception {
        String text = "암호화 테스트";
        String encryptedText = MyEncryptDecrypt.encrypt(text);
        String decryptedText = MyEncryptDecrypt.decrypt(encryptedText);

        System.out.println("원본 데이터: " + text);
        System.out.println("암호화된 데이터: " + encryptedText);
        System.out.println("복호화된 데이터: " + decryptedText);
    }
}