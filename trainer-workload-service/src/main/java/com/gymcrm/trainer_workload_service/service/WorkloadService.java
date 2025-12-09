//package com.gymcrm.trainer_workload_service.service;
//
//import com.gymcrm.trainer_workload_service.dto.ActionType;
//import com.gymcrm.trainer_workload_service.dto.TrainerWorkloadRequest;
//import com.gymcrm.trainer_workload_service.entity.MonthSummary;
//import com.gymcrm.trainer_workload_service.entity.TrainerWorkload;
//import com.gymcrm.trainer_workload_service.entity.YearSummary;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//@Slf4j
//public class WorkloadService {
//
//    // Имитация базы данных в памяти
//    private final Map<String, TrainerWorkload> workloadMap = new ConcurrentHashMap<>();
//
//    public void updateWorkload(TrainerWorkloadRequest request) {
//        log.info("Updating workload for trainer: {}", request.getUsername());
//
//        workloadMap.compute(request.getUsername(), (username, workload) -> {
//            if (workload == null) {
//                workload = new TrainerWorkload(
//                        username,
//                        request.getFirstName(),
//                        request.getLastName(),
//                        Boolean.TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE",
//                        new ArrayList<>()
//                );
//            } else {
//                // Обновляем базовую инфо
//                workload.setFirstName(request.getFirstName());
//                workload.setLastName(request.getLastName());
//                workload.setStatus(Boolean.TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE");
//            }
//
//            recalculateDuration(workload, request);
//            return workload;
//        });
//    }
//
//    public TrainerWorkload getWorkload(String username) {
//        return workloadMap.get(username);
//    }
//
//    private void recalculateDuration(TrainerWorkload workload, TrainerWorkloadRequest request) {
//        int year = request.getTrainingDate().getYear();
//        String month = request.getTrainingDate().getMonth().name(); // JANUARY, FEBRUARY...
//        int duration = request.getTrainingDuration();
//
//        // Находим или создаем год
//        YearSummary yearSummary = workload.getYears().stream()
//                .filter(y -> y.getYear() == year)
//                .findFirst()
//                .orElseGet(() -> {
//                    YearSummary newYear = new YearSummary(year, new ArrayList<>());
//                    workload.getYears().add(newYear);
//                    return newYear;
//                });
//
//        // Находим или создаем месяц
//        MonthSummary monthSummary = yearSummary.getMonths().stream()
//                .filter(m -> m.getMonth().equalsIgnoreCase(month))
//                .findFirst()
//                .orElseGet(() -> {
//                    MonthSummary newMonth = new MonthSummary(month, 0);
//                    yearSummary.getMonths().add(newMonth);
//                    return newMonth;
//                });
//
//        // Считаем
//        if (request.getActionType() == ActionType.ADD) {
//            monthSummary.setTrainingSummaryDuration(monthSummary.getTrainingSummaryDuration() + duration);
//        } else if (request.getActionType() == ActionType.DELETE) {
//            int newDuration = monthSummary.getTrainingSummaryDuration() - duration;
//            monthSummary.setTrainingSummaryDuration(Math.max(0, newDuration)); // Не уходим в минус
//        }
//    }
//}

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
        String username = request.getUsername();
        int year = request.getTrainingDate().getYear();
        String month = request.getTrainingDate().getMonth().name();
        int duration = request.getTrainingDuration();

        TrainerWorkload workload = workloadMap.get(username);

        if (workload == null) {
            workload = new TrainerWorkload(
                    username,
                    request.getFirstName(),
                    request.getLastName(),
                    Boolean.TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE",
                    new ArrayList<>()
            );
            workloadMap.put(username, workload);
        } else {
            workload.setFirstName(request.getFirstName());
            workload.setLastName(request.getLastName());
            workload.setStatus(Boolean.TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE");
        }

        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElse(null);

        if (yearSummary == null) {
            yearSummary = new YearSummary(year, new ArrayList<>());
            workload.getYears().add(yearSummary);
        }

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonth().equalsIgnoreCase(month))
                .findFirst()
                .orElse(null);

        if (monthSummary == null) {
            monthSummary = new MonthSummary(month, 0);
            yearSummary.getMonths().add(monthSummary);
        }

        monthSummary.setTrainingSummaryDuration(
                monthSummary.getTrainingSummaryDuration() + duration
        );
    }

    private void handleDelete(TrainerWorkloadRequest request) {
        String username = request.getUsername();
        int year = request.getTrainingDate().getYear();
        String month = request.getTrainingDate().getMonth().name();
        int duration = request.getTrainingDuration();

        TrainerWorkload workload = workloadMap.get(username);
        if (workload == null) {
            throw new TrainerNotFoundException("Trainer " + username + " not found");
        }

        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() ->
                        new InvalidDeleteException("Year " + year + " has no records for this trainer"));

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonth().equalsIgnoreCase(month))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidDeleteException("Month " + month + " has no records for this trainer"));

        int current = monthSummary.getTrainingSummaryDuration();

        if (duration > current) {
            throw new InvalidDeleteException(
                    "Cannot delete " + duration + " hours. Only " + current + " exist."
            );
        }

        monthSummary.setTrainingSummaryDuration(current - duration);
    }
}
