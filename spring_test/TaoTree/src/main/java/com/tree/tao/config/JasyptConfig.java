package com.tree.tao.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String jasyptEncryptorPassword;

    @Bean
    public StringEncryptor jasyptStringEncryptor() {
        log.info("########### jasyptStringEncryptor = {}", jasyptEncryptorPassword);
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 암호화 키 설정
        String password = System.getenv("JASYPT_PASSWORD"); // 환경 변수에서 가져오기
        if (password == null) {
            password = this.jasyptEncryptorPassword; // 시스템 프로퍼티에서 가져오기
        }
        System.out.println("password = " + password);

        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;

    }
}