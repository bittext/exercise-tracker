package com.spbmi.exercise.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ExerciseForm {

    @NotNull
    private LocalDate loggedOn = LocalDate.now();

    @NotBlank
    @Size(max = 200)
    private String activity;

    @NotNull
    @Min(1)
    @Max(1440)
    private Integer durationMinutes = 30;

    @Size(max = 500)
    private String note;

    public LocalDate getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(LocalDate loggedOn) {
        this.loggedOn = loggedOn;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
