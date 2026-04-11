package com.spbmi.exercise.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeightForm {

    @NotNull
    private LocalDate loggedOn = LocalDate.now();

    @NotNull
    @DecimalMin(value = "20.0", message = "Enter a realistic weight (kg)")
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
