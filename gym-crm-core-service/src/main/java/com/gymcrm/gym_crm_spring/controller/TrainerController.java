package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dto.TrainerActivationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerShortResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerTrainingsListResponse;
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
@RequestMapping("api/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Management")
@Validated
public class TrainerController {

    private final GymFacade gymFacade;

    @Operation(summary = "Get Trainer Profile", description = "Returns Trainer profile information by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile")
    public TrainerProfileResponse getProfile() {
        return gymFacade.getTrainerProfile(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Operation(summary = "Update Trainer Profile", description = "Update trainer profile information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/profile")
    @PreAuthorize("#request.username == authentication.name")
    public TrainerProfileUpdateResponse updateProfile(
            @Valid @RequestBody TrainerProfileUpdateRequest request
    ) {
        return gymFacade.updateTrainerProfile(request);
    }

    @Operation(
            summary = "Get not assigned active trainers",
            description = "Returns all active trainers who are not assigned to the specified trainee"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of not assigned active trainers retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/not-assigned")
    public List<TrainerShortResponse> getNotAssignedActiveTrainers(
            @RequestParam @NotBlank String username
    ) {
        return gymFacade.getNotAssignedActiveTrainers(username);
    }

    @Operation(summary = "Get Trainer Trainings List",
            description = "Returns list of trainer trainings filtered by date and trainee name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings list retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/trainings")
    public TrainerTrainingsListResponse getTrainerTrainingsList(
            @RequestParam @NotBlank String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        return gymFacade.getTrainerTrainingsList(username, periodFrom, periodTo, traineeName);
    }

    @Operation(summary = "Activate or Deactivate Trainer",
            description = "Activates or deactivates a trainer by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer activation status updated successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/activate")
    @PreAuthorize("#request.username == authentication.name")
    public void activateTrainer(
            @Valid @RequestBody TrainerActivationRequest request
    ) {
        gymFacade.activateTrainer(request);
    }
}
