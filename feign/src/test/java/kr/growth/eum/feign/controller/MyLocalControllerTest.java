package kr.growth.eum.feign.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class MyLocalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getData() throws Exception {
        mockMvc.perform(get("/api/local/data-01"))
                .andExpect(status().isOk())
                .andDo((result) -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertEquals("Test data from local service", response.getContentAsString());
                });
    }

    @Test
    void getTestData() throws Exception {
        mockMvc.perform(get("/api/local/data-02"))
                .andExpect(status().isOk())
                .andDo((result) -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertEquals("Test data from local service", response.getContentAsString());
                });
    }
}
