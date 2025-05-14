package comeon.playerservice.assignment.controller;

import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/players";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "secret";
    private static final String TEST_NAME = "John";
    private static final String TEST_SURNAME = "Doe";
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String TEST_ADDRESS = "123 Main St";

    @Test
    void testPlayerRegistrationSuccess() throws Exception {
        PlayerRegistrationRequest request = new PlayerRegistrationRequest();
        request.email = TEST_EMAIL;
        request.password = TEST_PASSWORD;
        request.name = TEST_NAME;
        request.surname = TEST_SURNAME;
        request.dateOfBirth = TEST_DATE_OF_BIRTH;
        request.address = TEST_ADDRESS;

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void testPlayerLoginSuccess() throws Exception {
        // First, register the player
        PlayerRegistrationRequest request = new PlayerRegistrationRequest();
        request.email = TEST_EMAIL;
        request.password = TEST_PASSWORD;
        request.name = TEST_NAME;
        request.surname = TEST_SURNAME;
        request.dateOfBirth = TEST_DATE_OF_BIRTH;
        request.address = TEST_ADDRESS;

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then, login with the same credentials
        String loginPayload = String.format("""
                {
                  "email": "%s",
                  "password": "%s"
                }
                """, TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.loginTime").exists());
    }

}
