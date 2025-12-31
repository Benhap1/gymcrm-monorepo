package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dto.TrainingCreateRequest;
import com.gymcrm.gym_crm_spring.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/training")
@RequiredArgsConstructor
@Tag(name = "Training Management")
@Validated
public class TrainingController {

    private final GymFacade gymFacade;

    @Operation(
            summary = "Add Training",
            description = "Creates a new training record for a trainee and trainer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    @PreAuthorize("#request.trainerUsername == authentication.name")
    public void addTraining(
            @Valid @RequestBody TrainingCreateRequest request
    ) {
        gymFacade.addTraining(request);
    }
}
