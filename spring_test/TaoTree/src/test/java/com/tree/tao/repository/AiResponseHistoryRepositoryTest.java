package com.tree.tao.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiResponseHistoryRepositoryTest {

    @Autowired private AiResponseHistoryRepository aiResponseHistoryRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void t_AiResponseHistoryRepositoryTest_true_00() {
        aiResponseHistoryRepository.findAll().forEach(System.out::println);
    }
}