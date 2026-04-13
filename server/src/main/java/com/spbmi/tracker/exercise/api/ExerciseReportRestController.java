package com.spbmi.tracker.exercise.api;

import com.spbmi.tracker.exercise.api.dto.ExerciseDailyReportDto;
import com.spbmi.tracker.exercise.api.dto.ExerciseMonthlyReportDto;
import com.spbmi.tracker.exercise.service.ExerciseReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/exercise/reports")
public class ExerciseReportRestController {

    private final ExerciseReportService exerciseReportService;

    public ExerciseReportRestController(ExerciseReportService exerciseReportService) {
        this.exerciseReportService = exerciseReportService;
    }

    @GetMapping("/daily")
    public ExerciseDailyReportDto daily(@RequestParam(required = false) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return exerciseReportService.buildDaily(d);
    }

    @GetMapping("/monthly")
    public ExerciseMonthlyReportDto monthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        YearMonth ym = (year != null && month != null && month >= 1 && month <= 12)
                ? YearMonth.of(year, month)
                : YearMonth.now();
        return exerciseReportService.buildMonthly(ym.getYear(), ym.getMonthValue());
    }
}
