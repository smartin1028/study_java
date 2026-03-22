package kr.growth.eum.feign.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE_64_ENCODER = Base64.getUrlEncoder();

    public static String generateBearerToken() {
        // 토큰 길이 설정
        int tokenLength = 1000;
        return TokenUtils.generateBearerToken(tokenLength);
    }

    public static String generateBearerToken(int tokenByteLength) {
        // 랜덤 문자열 생성
        byte[] randomBytes = new byte[tokenByteLength];
        SECURE_RANDOM.nextBytes(randomBytes);
        String randomToken = BASE_64_ENCODER.encodeToString(randomBytes).replaceAll("[^a-zA-Z0-9]", "");
        // "eum-" 접두사를 추가하고 전체 토큰 생성
        return "eum-" + randomToken;
    }

}
