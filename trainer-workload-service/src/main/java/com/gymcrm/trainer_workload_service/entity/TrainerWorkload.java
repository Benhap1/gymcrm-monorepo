package com.gymcrm.trainer_workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkload {
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private List<YearSummary> years;
}