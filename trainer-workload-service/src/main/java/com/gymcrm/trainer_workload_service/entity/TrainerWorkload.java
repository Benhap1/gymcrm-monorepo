package com.gymcrm.trainer_workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkload {
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private List<YearSummary> years;
}
