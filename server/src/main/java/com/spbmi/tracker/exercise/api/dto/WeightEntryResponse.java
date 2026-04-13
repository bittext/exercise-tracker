package com.spbmi.tracker.exercise.api.dto;

import com.spbmi.tracker.exercise.model.WeightEntry;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightEntryResponse(Long id, LocalDate loggedOn, BigDecimal weightKg, String note) {
    public static WeightEntryResponse from(WeightEntry w) {
        return new WeightEntryResponse(w.getId(), w.getLoggedOn(), w.getWeightKg(), w.getNote());
    }
}
