package com.tree.tao.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
//@SpringBootTest
class KeyInfoTest {
/*
jasypt.encryptor.password
 */
    @Value("${app.constants.api-version}")
    private String apiVersion;


    @Test
    void generateAESKey() {
        log.info(KeyInfo.getGenerateAESKeyToString());
    }

    /**
     * 암화화 관련 테스트
     */
    @Test
    public void stringEncryptorTest01() {
        log.info(apiVersion);
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        // 암호화 키 지정
        String property = System.getenv("JASYPT_PASSWORD"); //
        property = StringUtils.defaultIfEmpty(property, "This is an encryption key. It must never be exposed or made public.");
        System.out.println("property = " + property);
        config.setPassword(property);
        // 사용할 암호화 알고리즘을 지정
        config.setAlgorithm("PBEWithMD5AndDES");
        // 키 생성 시 반복 횟수를 설정. 반복 횟수가 많을수록 보안은 강화되지만 성능은 저하될 수 있음
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        // 솔트 생성 방식을 지정. RandomSaltGenerator는 매 암호화마다 무작위 솔트를 생성하여 보안성을 높임
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        // 암호화된 결과물의 인코딩 방식을 지정
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        // 암호화
        String str = "sa";
        String encrypt = encryptor.encrypt(str);
        // 복호화
        String decrypt = encryptor.decrypt(encrypt);
        log.info(encrypt);

        Assertions.assertEquals(str, decrypt);
    }

}