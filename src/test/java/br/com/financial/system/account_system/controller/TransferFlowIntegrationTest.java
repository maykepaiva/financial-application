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
class TransferFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetState() throws Exception {
        mockMvc.perform(post("/reset"));
    }

    @Test
    void returnsNotFoundForNonExistingOrigin() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\",\"origin\":\"200\",\"destination\":\"300\",\"amount\":15}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    void returnsBadRequestWhenBalanceIsInsufficient() throws Exception {
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\",\"origin\":\"100\",\"destination\":\"300\",\"amount\":50}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void movesBalanceFromOriginToDestination() throws Exception {
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":15}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\",\"origin\":\"100\",\"destination\":\"300\",\"amount\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.balance").value(0))
                .andExpect(jsonPath("$.destination.id").value("300"))
                .andExpect(jsonPath("$.destination.balance").value(15));

        mockMvc.perform(get("/balance").param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        mockMvc.perform(get("/balance").param("account_id", "300"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void createsDestinationAccountWhenItDoesNotExist() throws Exception {
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"));

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\",\"origin\":\"100\",\"destination\":\"999\",\"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("999"))
                .andExpect(jsonPath("$.destination.balance").value(10));
    }
}
