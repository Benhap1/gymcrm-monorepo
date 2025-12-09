package com.gymcrm.trainer_workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthSummary {
    private String month;
    private int trainingSummaryDuration;
}
