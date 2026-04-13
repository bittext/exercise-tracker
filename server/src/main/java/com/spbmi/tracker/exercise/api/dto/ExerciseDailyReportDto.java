package com.spbmi.tracker.exercise.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ExerciseDailyReportDto(
        LocalDate date,
        WeightEntryResponse weight,
        List<ExerciseEntryResponse> exercises,
        int sessionCount,
        int totalMinutes,
        Map<String, Integer> minutesByActivity
) {
}
