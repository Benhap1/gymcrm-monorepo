package com.gymcrm.trainer_workload_service.controller;

import com.gymcrm.trainer_workload_service.dto.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.entity.TrainerWorkload;
import com.gymcrm.trainer_workload_service.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workload")
@RequiredArgsConstructor
@Slf4j
public class WorkloadController {

    private final WorkloadService workloadService;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request) {
        log.info("Received request to update workload. Action: {}", request.getActionType());
        workloadService.updateWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<TrainerWorkload> getWorkload(@RequestParam("username") String username) {
        log.info("Received request to get workload for user: {}", username);

        TrainerWorkload workload = workloadService.getWorkload(username);
        if (workload == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(workload);
    }
}
