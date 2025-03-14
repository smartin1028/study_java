package com.tree.tao.config;

import lombok.AllArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

//@AllArgsConstructor
//@Component
public class JasyptEncryptor {

    private StringEncryptor jasyptStringEncryptor;

    public String encrypt(String value) {
        return jasyptStringEncryptor.encrypt(value);
    }

    public String decrypt(String encryptedValue) {
        return jasyptStringEncryptor.decrypt(encryptedValue);
    }
}
