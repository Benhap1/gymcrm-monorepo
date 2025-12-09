package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dao.TrainingDao;
import com.gymcrm.gym_crm_spring.dao.TraineeDao;
import com.gymcrm.gym_crm_spring.dao.TrainerDao;
import com.gymcrm.gym_crm_spring.dao.TrainingTypeDao;
import com.gymcrm.gym_crm_spring.dao.UserDao;
import com.gymcrm.gym_crm_spring.domain.TrainingType;
import com.gymcrm.gym_crm_spring.dto.LoginRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationResponse;
import com.gymcrm.gym_crm_spring.dto.TrainingCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TrainingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    private ObjectMapper objectMapper;

    private String traineeToken;
    private String trainerToken;
    private String traineeUsername;
    private String trainerUsername;

    @BeforeEach
    void setUp() throws Exception {
        userDao.findAll().forEach(user -> userDao.delete(user.getId()));
        traineeDao.findAll().forEach(trainee -> traineeDao.delete(trainee.getId()));
        trainerDao.findAll().forEach(trainer -> trainerDao.delete(trainer.getId()));
        trainingDao.findAll().forEach(training -> trainingDao.delete(training.getId()));

        if (trainingTypeDao.findByName("Strength").isEmpty()) {
            TrainingType tt = new TrainingType();
            tt.setTrainingTypeName("Strength");
            trainingTypeDao.save(tt);
        }

        var regTraineeRequest = new TraineeRegistrationRequest("John", "Doe", Optional.empty(), Optional.empty());
        MvcResult regTraineeResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regTraineeRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regTraineeResponse = objectMapper.readValue(regTraineeResult.getResponse().getContentAsString(), TraineeRegistrationResponse.class);
        this.traineeUsername = regTraineeResponse.username();
        String traineePassword = regTraineeResponse.password();

        var loginTraineeRequest = new LoginRequest(traineeUsername, traineePassword);
        MvcResult loginTraineeResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginTraineeRequest)))
                .andExpect(status().isOk())
                .andReturn();
        var loginTraineeResponse = objectMapper.readValue(loginTraineeResult.getResponse().getContentAsString(), Map.class);
        this.traineeToken = (String) loginTraineeResponse.get("token");

        var regTrainerRequest = new TrainerRegistrationRequest("Mike", "Smith", "Strength");
        MvcResult regTrainerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regTrainerRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var regTrainerResponse = objectMapper.readValue(regTrainerResult.getResponse().getContentAsString(), TrainerRegistrationResponse.class);
        this.trainerUsername = regTrainerResponse.username();
        String trainerPassword = regTrainerResponse.password();

        var loginTrainerRequest = new LoginRequest(trainerUsername, trainerPassword);
        MvcResult loginTrainerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginTrainerRequest)))
                .andExpect(status().isOk())
                .andReturn();
        var loginTrainerResponse = objectMapper.readValue(loginTrainerResult.getResponse().getContentAsString(), Map.class);
        this.trainerToken = (String) loginTrainerResponse.get("token");
    }

    @Test
    @DisplayName("POST /api/training/add — add training successfully")
    void addTraining_Success() throws Exception {
        var request = new TrainingCreateRequest(
                traineeUsername,
                trainerUsername,
                "Sample Training",
                LocalDate.now(),
                60
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/training/add")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var trainings = trainingDao.findByCriteriaForTrainee(traineeUsername, null, null, null, null);
        assertThat(trainings).hasSize(1);
        var training = trainings.get(0);
        assertThat(training.getTrainingName()).isEqualTo("Sample Training");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.now());
        assertThat(training.getTrainingDuration()).isEqualTo(60);
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo(traineeUsername);
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo(trainerUsername);
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Strength");
    }

    @Test
    @DisplayName("POST /api/training/add — fail on invalid token")
    void addTraining_InvalidToken_Fails() throws Exception {
        var request = new TrainingCreateRequest(
                traineeUsername,
                trainerUsername,
                "Sample Training",
                LocalDate.now(),
                60
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/training/add")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        var trainings = trainingDao.findByCriteriaForTrainee(traineeUsername, null, null, null, null);
        assertThat(trainings).isEmpty();
    }

    @Test
    @DisplayName("POST /api/training/add — fail on invalid trainee")
    void addTraining_InvalidTrainee_Fails() throws Exception {
        var request = new TrainingCreateRequest(
                "invalid-trainee",
                trainerUsername,
                "Sample Training",
                LocalDate.now(),
                60
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/training/add")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        var trainings = trainingDao.findByCriteriaForTrainee("invalid-trainee", null, null, null, null);
        assertThat(trainings).isEmpty();
    }

    @Test
    @DisplayName("POST /api/training/add — fail on invalid trainer")
    void addTraining_InvalidTrainer_Fails() throws Exception {
        var request = new TrainingCreateRequest(
                traineeUsername,
                "invalid-trainer",
                "Sample Training",
                LocalDate.now(),
                60
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/training/add")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        var trainings = trainingDao.findByCriteriaForTrainee(traineeUsername, null, null, null, null);
        assertThat(trainings).isEmpty();
    }

    @Test
    @DisplayName("POST /api/training/add — fail on validation error (empty name)")
    void addTraining_ValidationError_Fails() throws Exception {
        var request = new TrainingCreateRequest(
                traineeUsername,
                trainerUsername,
                "",
                LocalDate.now(),
                60
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/training/add")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        var trainings = trainingDao.findByCriteriaForTrainee(traineeUsername, null, null, null, null);
        assertThat(trainings).isEmpty();
    }
}