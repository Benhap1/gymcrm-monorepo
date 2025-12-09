package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dto.TrainingTypeResponse;
import com.gymcrm.gym_crm_spring.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@Tag(name = "Training Types Management")
@Validated
public class TrainingTypeController {

    private final GymFacade gymFacade;

    @Operation(
            summary = "Get all training types",
            description = "Returns a list of all available training types"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        return gymFacade.getAllTrainingTypes();
    }
}
