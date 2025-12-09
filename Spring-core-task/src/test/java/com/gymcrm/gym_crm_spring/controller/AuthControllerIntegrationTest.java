package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dao.TraineeDao;
import com.gymcrm.gym_crm_spring.dao.TrainerDao;
import com.gymcrm.gym_crm_spring.dao.TrainingTypeDao;
import com.gymcrm.gym_crm_spring.dao.UserDao;
import com.gymcrm.gym_crm_spring.domain.TrainingType;
import com.gymcrm.gym_crm_spring.dto.ChangePasswordRequest;
import com.gymcrm.gym_crm_spring.dto.LoginRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    private ObjectMapper objectMapper;

       @BeforeEach
    void setUp() {
        if (trainingTypeDao.findByName("Strength").isEmpty()) {
            TrainingType tt = new TrainingType();
            tt.setTrainingTypeName("Strength");
            trainingTypeDao.save(tt);
        }

        userDao.findAll().forEach(user -> userDao.delete(user.getId()));
        traineeDao.findAll().forEach(trainee -> traineeDao.delete(trainee.getId()));
        trainerDao.findAll().forEach(trainer -> trainerDao.delete(trainer.getId()));
    }

    @Test
    @DisplayName("POST /api/auth/register/trainee — register new Trainee successfully")
    void registerTrainee_Success() throws Exception {
        var request = new TraineeRegistrationRequest(
                "John",
                "Doe",
                Optional.of(LocalDate.of(1990, 1, 1)),
                Optional.of("New York")
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseJson, TraineeRegistrationResponse.class);

        assertThat(response.username()).isNotBlank();
        assertThat(response.password()).isNotBlank();

        var userOpt = userDao.findByUsername(response.username());
        assertThat(userOpt).isPresent();
        var user = userOpt.get();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getActive()).isTrue();

        var traineeOpt = traineeDao.findByUsername(response.username());
        assertThat(traineeOpt).isPresent();
        var trainee = traineeOpt.get();
        assertThat(trainee.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(trainee.getAddress()).isEqualTo("New York");
        assertThat(trainee.getUser()).isEqualTo(user);

        assertThat(new BCryptPasswordEncoder().matches(response.password(), user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/register/trainee — fail on duplicate user")
    void registerTrainee_Duplicate_Fails() throws Exception {

        var firstRequest = new TraineeRegistrationRequest("John", "Doe", Optional.empty(), Optional.empty());
        MvcResult firstResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var firstResponseJson = firstResult.getResponse().getContentAsString();
        objectMapper.readValue(firstResponseJson, TraineeRegistrationResponse.class);

        var duplicateRequest = new TraineeRegistrationRequest("John", "Doe", Optional.empty(), Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is already registered as Trainee or Trainer"));


        assertThat(userDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/auth/register/trainer — register new Trainer successfully")
    void registerTrainer_Success() throws Exception {
        var request = new TrainerRegistrationRequest(
                "Mike",
                "Smith",
                "Strength"
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseJson, TrainerRegistrationResponse.class);

        assertThat(response.username()).isNotBlank();
        assertThat(response.password()).isNotBlank();

        var userOpt = userDao.findByUsername(response.username());
        assertThat(userOpt).isPresent();
        var user = userOpt.get();
        assertThat(user.getFirstName()).isEqualTo("Mike");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getActive()).isTrue();

        var trainerOpt = trainerDao.findByUsername(response.username());
        assertThat(trainerOpt).isPresent();
        var trainer = trainerOpt.get();
        assertThat(trainer.getSpecialization().getTrainingTypeName()).isEqualTo("Strength");
        assertThat(trainer.getUser()).isEqualTo(user);

        assertThat(new BCryptPasswordEncoder().matches(response.password(), user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/register/trainer — fail on duplicate user")
    void registerTrainer_Duplicate_Fails() throws Exception {

        var firstRequest = new TrainerRegistrationRequest("Mike", "Smith", "Strength");
        MvcResult firstResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var firstResponseJson = firstResult.getResponse().getContentAsString();
        objectMapper.readValue(firstResponseJson, TrainerRegistrationResponse.class);

        var duplicateRequest = new TrainerRegistrationRequest("Mike", "Smith", "Strength");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());

        assertThat(userDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/auth/login — login user successfully")
    void login_Success() throws Exception {

        var regRequest = new TraineeRegistrationRequest("Jane", "Doe", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var loginRequest = new LoginRequest(regResponse.username(), regResponse.password());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseJson = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginResponseJson, java.util.Map.class);

        assertThat(loginResponse.get("token")).isNotNull();
        assertThat(loginResponse.get("token")).isInstanceOf(String.class);
        assertThat((String) loginResponse.get("token")).isNotBlank();
    }

    @Test
    @DisplayName("POST /api/auth/login — fail on invalid credentials")
    void login_InvalidCredentials_Fails() throws Exception {

        var regRequest = new TraineeRegistrationRequest("Jane", "Doe", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var invalidLoginRequest = new LoginRequest(regResponse.username(), "wrongpass");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/auth/change-login — change password successfully")
    void changeLogin_Success() throws Exception {

        var regRequest = new TraineeRegistrationRequest("Alice", "Wonder", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var loginRequest = new LoginRequest(regResponse.username(), regResponse.password());
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseJson = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginResponseJson, java.util.Map.class);
        String token = (String) loginResponse.get("token");

        var changeRequest = new ChangePasswordRequest(regResponse.username(), regResponse.password(), "newSecurePass456");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/change-login")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isOk());

        var userOpt = userDao.findByUsername(regResponse.username());
        assertThat(userOpt).isPresent();
        var user = userOpt.get();
        assertThat(new BCryptPasswordEncoder().matches(regResponse.password(), user.getPassword())).isFalse();
        assertThat(new BCryptPasswordEncoder().matches("newSecurePass456", user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("PUT /api/auth/change-login — fail on invalid old password")
    void changeLogin_InvalidOldPassword_Fails() throws Exception {
        var regRequest = new TraineeRegistrationRequest("Bob", "Builder", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var loginRequest = new LoginRequest(regResponse.username(), regResponse.password());
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseJson = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginResponseJson, java.util.Map.class);
        String token = (String) loginResponse.get("token");

        var invalidRequest = new ChangePasswordRequest(regResponse.username(), "wrongOldPass", "newPass");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/change-login")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());


        var userOpt = userDao.findByUsername(regResponse.username());
        assertThat(userOpt).isPresent();
        var user = userOpt.get();
        assertThat(new BCryptPasswordEncoder().matches(regResponse.password(), user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("PUT /api/auth/change-login — fail on non-existent user")
    void changeLogin_NonExistentUser_Fails() throws Exception {
        var regRequest = new TraineeRegistrationRequest("Test", "User", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var loginRequest = new LoginRequest(regResponse.username(), regResponse.password());
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseJson = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginResponseJson, java.util.Map.class);
        String token = (String) loginResponse.get("token");

        var request = new ChangePasswordRequest("nonexistent", "old", "new");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/change-login")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertThat(userDao.findByUsername("nonexistent")).isEmpty();
    }


    @Test
    @DisplayName("POST /api/auth/logout — logout user successfully")
    void logout_Success() throws Exception {

        var regRequest = new TraineeRegistrationRequest("LogoutTest", "User", Optional.empty(), Optional.empty());
        MvcResult regResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regResponseJson = regResult.getResponse().getContentAsString();
        var regResponse = objectMapper.readValue(regResponseJson, TraineeRegistrationResponse.class);

        var loginRequest = new LoginRequest(regResponse.username(), regResponse.password());
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseJson = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginResponseJson, java.util.Map.class);
        String token = (String) loginResponse.get("token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/training-types")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}