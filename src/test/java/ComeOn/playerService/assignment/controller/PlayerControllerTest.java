package comeon.playerservice.assignment.controller;

import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    mockMvc.perform(post(BASE_URL + "/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRegistrationRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(TEST_EMAIL));
  }

  @Test
  void testPlayerLoginSuccess() throws Exception {
    registerPlayer();

    mockMvc.perform(post(BASE_URL + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createLoginPayload()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.loginTime").exists());
  }

  @Test
  void testPlayerLogoutSuccess() throws Exception {
    // Register and login
    registerPlayer();
    Long sessionId = loginAndGetSessionId();

    String logoutPayload = String.format("{\"sessionId\": %d}", sessionId);

    mockMvc.perform(post(BASE_URL + "/logout")
        .content(logoutPayload)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Player logged out successfully"));
  }

  @Test
  void testSetTimeLimitSuccess() throws Exception {
    registerPlayer();
    loginAndGetSessionId();

    mockMvc.perform(post(BASE_URL + "/set-daily-limit")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createTimeLimitPayload(60)))
        .andExpect(status().isOk())
        .andExpect(content().string("Time limit set successfully"));
  }

  @Test
  void testLoginFailsWhenLimitReached() throws Exception {
    registerPlayer();
    loginAndGetSessionId();

    mockMvc.perform(post(BASE_URL + "/set-daily-limit")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createTimeLimitPayload(0)))
        .andExpect(status().isOk());

    mockMvc.perform(post(BASE_URL + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createLoginPayload()))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message").value("Daily time limit reached"));
  }

  private PlayerRegistrationRequest createRegistrationRequest() {
    PlayerRegistrationRequest request = new PlayerRegistrationRequest();
    request.email = TEST_EMAIL;
    request.password = TEST_PASSWORD;
    request.name = TEST_NAME;
    request.surname = TEST_SURNAME;
    request.dateOfBirth = TEST_DATE_OF_BIRTH;
    request.address = TEST_ADDRESS;
    return request;
  }

  private String createLoginPayload() {
    return String.format("""
        {
          "email": "%s",
          "password": "%s"
        }
        """, TEST_EMAIL, TEST_PASSWORD);
  }

  private String createTimeLimitPayload(int dailyLimitMinutes) {
    return String.format("""
        {
          "email": "%s",
          "dailyLimitMinutes": %d
        }
        """, TEST_EMAIL, dailyLimitMinutes);
  }

  private void registerPlayer() throws Exception {
    mockMvc.perform(post(BASE_URL + "/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRegistrationRequest())))
        .andExpect(status().isOk());
  }

  private Long loginAndGetSessionId() throws Exception {
    String sessionResponse = mockMvc.perform(post(BASE_URL + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createLoginPayload()))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    return objectMapper.readTree(sessionResponse).get("id").asLong();
  }
}
