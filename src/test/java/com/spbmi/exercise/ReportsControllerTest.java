package com.spbmi.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:reportsdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void monthlyReportsPageLoads() throws Exception {
        mockMvc.perform(get("/reports").param("mode", "monthly"))
                .andExpect(status().isOk());
    }

    @Test
    void dailyReportsPageLoads() throws Exception {
        mockMvc.perform(get("/reports").param("mode", "daily").param("date", "2026-04-11"))
                .andExpect(status().isOk());
    }
}
