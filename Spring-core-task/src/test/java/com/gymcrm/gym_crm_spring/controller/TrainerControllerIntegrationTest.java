package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dao.TraineeDao;
import com.gymcrm.gym_crm_spring.dao.TrainerDao;
import com.gymcrm.gym_crm_spring.dao.TrainingDao;
import com.gymcrm.gym_crm_spring.dao.TrainingTypeDao;
import com.gymcrm.gym_crm_spring.dao.UserDao;
import com.gymcrm.gym_crm_spring.domain.TrainingType;
import com.gymcrm.gym_crm_spring.dto.LoginRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerActivationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationResponse;
import com.gymcrm.gym_crm_spring.dto.TrainingCreateRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationResponse;
import com.gymcrm.gym_crm_spring.facade.GymFacade;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TrainerControllerIntegrationTest {

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
    private GymFacade gymFacade;

    @Autowired
    private ObjectMapper objectMapper;

    private String trainerToken;
    private String traineeToken;
    private String trainerUsername;
    private String traineeUsername;

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
    }

    @Test
    @DisplayName("GET /api/trainer/profile — get Trainer profile successfully")
    void getProfile_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/profile")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mike"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization").value("Strength"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees.length()").value(0));

        var trainerOpt = trainerDao.findByUsername(trainerUsername);
        assertThat(trainerOpt).isPresent();
    }

    @Test
    @DisplayName("GET /api/trainer/profile — fail on invalid token")
    void getProfile_InvalidToken_Fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/profile")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/trainer/profile — fail on missing token")
    void getProfile_MissingToken_Fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/trainer/profile — fail on wrong role (trainee token)")
    void getProfile_WrongRole_Fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/profile")
                        .header("Authorization", "Bearer " + traineeToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/trainer/profile — update Trainer profile successfully")
    void updateProfile_Success() throws Exception {
        var request = new TrainerProfileUpdateRequest(
                trainerUsername,
                "Michael",
                "Smith Jr",
                "Strength",
                true
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trainer/profile")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Michael"))
                .andExpect(jsonPath("$.lastName").value("Smith Jr"))
                .andExpect(jsonPath("$.specialization").value("Strength"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees.length()").value(0));

        var trainerOpt = trainerDao.findByUsername(trainerUsername);
        assertThat(trainerOpt).isPresent();
        var trainer = trainerOpt.get();
        var user = trainer.getUser();
        assertThat(user.getFirstName()).isEqualTo("Michael");
        assertThat(user.getLastName()).isEqualTo("Smith Jr");
        assertThat(user.getActive()).isTrue();
    }

    @Test
    @DisplayName("PUT /api/trainer/profile — fail on invalid token")
    void updateProfile_InvalidToken_Fails() throws Exception {
        var request = new TrainerProfileUpdateRequest(
                trainerUsername,
                "New",
                "Name",
                "Strength",
                true
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trainer/profile")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        var trainerOpt = trainerDao.findByUsername(trainerUsername);
        assertThat(trainerOpt).isPresent();
        var user = trainerOpt.get().getUser();
        assertThat(user.getFirstName()).isEqualTo("Mike");
    }

    @Test
    @DisplayName("GET /api/trainer/not-assigned — get not assigned active trainers successfully (as trainee)")
    void getNotAssignedActiveTrainers_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/not-assigned")
                        .header("Authorization", "Bearer " + traineeToken)
                        .param("username", traineeUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(trainerUsername))
                .andExpect(jsonPath("$[0].firstName").value("Mike"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].specialization").value("Strength"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/trainer/not-assigned — fail on invalid token")
    void getNotAssignedActiveTrainers_InvalidToken_Fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/not-assigned")
                        .header("Authorization", "Bearer invalid-token")
                        .param("username", traineeUsername))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/trainer/trainings — get Trainer trainings successfully (empty list)")
    void getTrainerTrainingsList_Empty_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/trainings")
                        .header("Authorization", "Bearer " + trainerToken)
                        .param("username", trainerUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainings").isArray())
                .andExpect(jsonPath("$.trainings.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/trainer/trainings — get Trainer trainings with filters (after creating)")
    void getTrainerTrainingsList_WithData_Success() throws Exception {
        var createRequest = new TrainingCreateRequest(
                traineeUsername,
                trainerUsername,
                "Sample Training",
                LocalDate.now(),
                60
        );
        gymFacade.addTraining(createRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/trainings")
                        .header("Authorization", "Bearer " + trainerToken)
                        .param("username", trainerUsername)
                        .param("periodFrom", LocalDate.now().minusDays(1).toString())
                        .param("periodTo", LocalDate.now().plusDays(1).toString())
                        .param("traineeName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainings[0].trainingName").value("Sample Training"))
                .andExpect(jsonPath("$.trainings[0].trainingDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.trainings[0].trainingType").value("Strength"))
                .andExpect(jsonPath("$.trainings[0].trainingDuration").value(60))
                .andExpect(jsonPath("$.trainings[0].traineeUsername").value(traineeUsername))
                .andExpect(jsonPath("$.trainings").isArray())
                .andExpect(jsonPath("$.trainings.length()").value(1));

        var trainings = trainingDao.findByCriteriaForTrainer(trainerUsername, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "John");
        assertThat(trainings).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/trainer/trainings — fail on invalid token")
    void getTrainerTrainingsList_InvalidToken_Fails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trainer/trainings")
                        .header("Authorization", "Bearer invalid-token")
                        .param("username", trainerUsername))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/trainer/activate — activate Trainer successfully")
    void activateTrainer_Success() throws Exception {
        var request = new TrainerActivationRequest(trainerUsername, false);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/trainer/activate")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var trainerOpt = trainerDao.findByUsername(trainerUsername);
        assertThat(trainerOpt).isPresent();
        assertThat(trainerOpt.get().getUser().getActive()).isFalse();
    }

    @Test
    @DisplayName("PATCH /api/trainer/activate — fail on invalid token")
    void activateTrainer_InvalidToken_Fails() throws Exception {
        var request = new TrainerActivationRequest(trainerUsername, false);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/trainer/activate")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        var trainerOpt = trainerDao.findByUsername(trainerUsername);
        assertThat(trainerOpt).isPresent();
        assertThat(trainerOpt.get().getUser().getActive()).isTrue();
    }
}