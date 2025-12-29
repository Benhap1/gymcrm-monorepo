package com.gymcrm.trainer_workload_service.service;

import com.gymcrm.trainer_workload_service.dto.ActionType;
import com.gymcrm.trainer_workload_service.dto.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.entity.MonthSummary;
import com.gymcrm.trainer_workload_service.entity.TrainerWorkload;
import com.gymcrm.trainer_workload_service.entity.YearSummary;
import com.gymcrm.trainer_workload_service.exception.InvalidDeleteException;
import com.gymcrm.trainer_workload_service.exception.TrainerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WorkloadService {

    private final Map<String, TrainerWorkload> workloadMap = new ConcurrentHashMap<>();

    public void updateWorkload(TrainerWorkloadRequest request) {
        log.info("Updating workload for trainer: {}", request.getUsername());

        if (request.getActionType() == ActionType.ADD) {
            handleAdd(request);
        } else if (request.getActionType() == ActionType.DELETE) {
            handleDelete(request);
        } else {
            throw new IllegalArgumentException("Invalid action type: " + request.getActionType());
        }
    }

    public TrainerWorkload getWorkload(String username) {
        return workloadMap.get(username);
    }

    private void handleAdd(TrainerWorkloadRequest request) {
        TrainerWorkload workload = workloadMap.computeIfAbsent(request.getUsername(),
                username -> new TrainerWorkload(
                        username,
                        request.getFirstName(),
                        request.getLastName(),
                        getStatus(request),
                        new ArrayList<>()
                )
        );

        updateTrainerInfo(workload, request);

        YearSummary yearSummary = getOrCreateYearSummary(workload.getYears(), request.getTrainingDate().getYear());
        MonthSummary monthSummary = getOrCreateMonthSummary(yearSummary.getMonths(), request.getTrainingDate().getMonth().name());

        monthSummary.setTrainingSummaryDuration(monthSummary.getTrainingSummaryDuration() + request.getTrainingDuration());
    }

    private void handleDelete(TrainerWorkloadRequest request) {
        TrainerWorkload workload = workloadMap.get(request.getUsername());
        if (workload == null) {
            throw new TrainerNotFoundException("Trainer " + request.getUsername() + " not found");
        }

        YearSummary yearSummary = findYearSummary(workload.getYears(), request.getTrainingDate().getYear());
        MonthSummary monthSummary = findMonthSummary(yearSummary.getMonths(), request.getTrainingDate().getMonth().name());

        int current = monthSummary.getTrainingSummaryDuration();
        if (request.getTrainingDuration() > current) {
            throw new InvalidDeleteException(
                    "Cannot delete " + request.getTrainingDuration() + " hours. Only " + current + " exist."
            );
        }

        monthSummary.setTrainingSummaryDuration(current - request.getTrainingDuration());
    }

    private String getStatus(TrainerWorkloadRequest request) {
        return Boolean.TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE";
    }

    private void updateTrainerInfo(TrainerWorkload workload, TrainerWorkloadRequest request) {
        workload.setFirstName(request.getFirstName());
        workload.setLastName(request.getLastName());
        workload.setStatus(getStatus(request));
    }

    private YearSummary getOrCreateYearSummary(List<YearSummary> years, int year) {
        return years.stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = new YearSummary(year, new ArrayList<>());
                    years.add(newYear);
                    return newYear;
                });
    }

    private MonthSummary getOrCreateMonthSummary(List<MonthSummary> months, String month) {
        return months.stream()
                .filter(m -> m.getMonth().equalsIgnoreCase(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = new MonthSummary(month, 0);
                    months.add(newMonth);
                    return newMonth;
                });
    }

    private YearSummary findYearSummary(List<YearSummary> years, int year) {
        return years.stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() -> new InvalidDeleteException("Year " + year + " has no records for this trainer"));
    }

    private MonthSummary findMonthSummary(List<MonthSummary> months, String month) {
        return months.stream()
                .filter(m -> m.getMonth().equalsIgnoreCase(month))
                .findFirst()
                .orElseThrow(() -> new InvalidDeleteException("Month " + month + " has no records for this trainer"));
    }
}
