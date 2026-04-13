package com.spbmi.tracker.exercise.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeightRequest {

    @NotNull
    private LocalDate loggedOn = LocalDate.now();

    @NotNull
    @DecimalMin("20.0")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal weightKg;

    @Size(max = 500)
    private String note;

    public LocalDate getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(LocalDate loggedOn) {
        this.loggedOn = loggedOn;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
