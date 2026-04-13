package com.spbmi.tracker.exercise.api.dto;

public record CalendarCellDto(
        boolean empty,
        String isoDate,
        Integer dayOfMonth,
        boolean today,
        boolean exercise,
        Integer exerciseMinutes,
        String weightKg
) {
}
