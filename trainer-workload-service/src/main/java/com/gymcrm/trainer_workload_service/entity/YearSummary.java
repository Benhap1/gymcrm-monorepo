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
public class YearSummary {
    private int year;
    private List<MonthSummary> months;
}