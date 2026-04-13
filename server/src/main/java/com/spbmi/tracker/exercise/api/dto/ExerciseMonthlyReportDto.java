package com.spbmi.tracker.exercise.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ExerciseMonthlyReportDto(
        int year,
        int month,
        LocalDate today,
        List<List<CalendarCellDto>> calendarWeeks,
        Set<String> exerciseDatesIso,
        Map<String, Integer> exerciseMinutesByIso,
        Map<String, String> weightKgByIso,
        int distinctExerciseDays,
        int exerciseSessions,
        int totalExerciseMinutes,
        int weightEntryCount,
        BigDecimal weightMin,
        BigDecimal weightMax,
        BigDecimal weightAvg,
        Map<String, Integer> minutesByActivity,
        List<WeightEntryResponse> weightRows
) {
}
