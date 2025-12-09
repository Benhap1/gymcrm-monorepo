package com.gymcrm.gym_crm_spring.controller;

import com.gymcrm.gym_crm_spring.dto.ChangePasswordRequest;
import com.gymcrm.gym_crm_spring.dto.LoginRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeRegistrationResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerRegistrationResponse;
import com.gymcrm.gym_crm_spring.exception.InvalidCredentialsException;
import com.gymcrm.gym_crm_spring.exception.LockedException;
import com.gymcrm.gym_crm_spring.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Registration")
@Validated
public class AuthController {

    private final GymFacade gymFacade;

    @Operation(summary = "Registration of new Trainee", description = "Create Trainee and return username")
    @ApiResponse(responseCode = "201", description = "Trainee registered successfully!")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register/trainee")
    public TraineeRegistrationResponse register(@Valid @RequestBody TraineeRegistrationRequest request) {
        return gymFacade.registerTrainee(request);

    }

    @Operation(summary = "Registration of new Trainer", description = "Create Trainer and return username")
    @ApiResponse(responseCode = "201", description = "Trainer registered successfully!")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register/trainer")
    public TrainerRegistrationResponse register(@Valid @RequestBody TrainerRegistrationRequest request) {
        return gymFacade.registerTrainer(request);

    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "423", description = "Account temporarily locked")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = gymFacade.login(request);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Logout (invalidate token)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        gymFacade.logout(authHeader);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change user password", description = "Change password for existing user")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/change-login")
    public void changeLogin(@Valid @RequestBody ChangePasswordRequest request) {
        gymFacade.changeLogin(request);

    }
}
