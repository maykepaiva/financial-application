package br.com.financial.system.account_system.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepositFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetState() throws Exception {
        mockMvc.perform(post("/reset"));
    }

    @Test
    void createsAccountOnFirstDeposit() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(10));

        mockMvc.perform(get("/balance").param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void accumulatesBalanceOnRepeatedDeposits() throws Exception {
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.balance").value(20));

        mockMvc.perform(get("/balance").param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void rejectsZeroAmount() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":0}"))
                .andExpect(status().isBadRequest());
    }
}
