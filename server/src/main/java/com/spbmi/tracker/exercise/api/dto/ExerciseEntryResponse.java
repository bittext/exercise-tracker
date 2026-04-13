package com.spbmi.tracker.exercise.api.dto;

import com.spbmi.tracker.exercise.model.ExerciseEntry;

import java.time.LocalDate;

public record ExerciseEntryResponse(
        Long id,
        LocalDate loggedOn,
        String activity,
        int durationMinutes,
        String note
) {
    public static ExerciseEntryResponse from(ExerciseEntry e) {
        return new ExerciseEntryResponse(
                e.getId(),
                e.getLoggedOn(),
                e.getActivity(),
                e.getDurationMinutes(),
                e.getNote()
        );
    }
}
