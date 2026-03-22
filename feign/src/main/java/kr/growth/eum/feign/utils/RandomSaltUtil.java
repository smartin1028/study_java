package kr.growth.eum.feign.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomSaltUtil {
    // 랜덤 salt 생성 메서드
    public static String generateRandomSalt() {
        return generateRandomSalt(24);
    }
    // 랜덤 salt 생성 메서드
    public static String generateRandomSalt(int byteSize) {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[byteSize]; // 24 bytes = 192 bits
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
