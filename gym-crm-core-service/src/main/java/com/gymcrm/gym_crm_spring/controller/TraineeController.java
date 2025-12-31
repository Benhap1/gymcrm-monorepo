package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dto.TraineeActivationRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainerListUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainerListUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainingResponse;
import com.gymcrm.gym_crm_spring.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee Management")
@Validated
public class TraineeController {

    private final GymFacade gymFacade;

    @Operation(summary = "Get Trainee Profile", description = "Returns Trainee profile information by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile")
    public TraineeProfileResponse getProfile() {
        return gymFacade.getTraineeProfile(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Operation(summary = "Update Trainee Profile", description = "Update trainee profile and return updated data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully")

    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/profile")
    @PreAuthorize("#request.username == authentication.name")
    public TraineeProfileUpdateResponse updateProfile(
            @Valid @RequestBody TraineeProfileUpdateRequest request
    ) {
        return gymFacade.updateTraineeProfile(request);
    }

    @Operation(summary = "Delete Trainee Profile", description = "Deletes trainee profile by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/profile")
    public void deleteProfile() {
        gymFacade.deleteTraineeProfile(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Operation(summary = "Update Trainee's Trainer List", description = "Updates the list of trainers assigned to a trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/trainers")
    @PreAuthorize("#request.traineeUsername == authentication.name")
    public TraineeTrainerListUpdateResponse updateTrainerList(
            @Valid @RequestBody TraineeTrainerListUpdateRequest request
    ) {
        return gymFacade.updateTraineeTrainerList(request);
    }

    @Operation(
            summary = "Get Trainee Trainings List",
            description = "Returns a list of trainings for a given trainee filtered by optional parameters (date range, trainer name, training type)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee trainings retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trainings")
    @PreAuthorize("#username == authentication.name")
    public List<TraineeTrainingResponse> getTraineeTrainings(
            @RequestParam @NotBlank String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType
    ) {
        return gymFacade.getTraineeTrainings(username, from, to, trainerName, trainingType);
    }

    @Operation(summary = "Activate or Deactivate Trainee",
            description = "Activates or deactivates a trainee by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee activation status updated successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/activate")
    @PreAuthorize("#request.username == authentication.name")
    public void activateTrainee(
            @Valid @RequestBody TraineeActivationRequest request
    ) {
        gymFacade.activateTrainee(request);
    }
}
