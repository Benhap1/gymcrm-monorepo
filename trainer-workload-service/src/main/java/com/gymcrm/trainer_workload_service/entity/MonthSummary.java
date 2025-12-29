package com.gymcrm.trainer_workload_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthSummary {
    private String month;
    private int trainingSummaryDuration;
}
