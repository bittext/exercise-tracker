package com.spbmi.tracker.exercise.api;

import com.spbmi.tracker.exercise.api.dto.ExerciseEntryResponse;
import com.spbmi.tracker.exercise.api.dto.ExerciseRequest;
import com.spbmi.tracker.exercise.api.dto.WeightEntryResponse;
import com.spbmi.tracker.exercise.api.dto.WeightRequest;
import com.spbmi.tracker.exercise.model.ExerciseEntry;
import com.spbmi.tracker.exercise.model.WeightEntry;
import com.spbmi.tracker.exercise.repo.ExerciseEntryRepository;
import com.spbmi.tracker.exercise.repo.WeightEntryRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseResourceController {

    private final WeightEntryRepository weightEntryRepository;
    private final ExerciseEntryRepository exerciseEntryRepository;

    public ExerciseResourceController(
            WeightEntryRepository weightEntryRepository,
            ExerciseEntryRepository exerciseEntryRepository) {
        this.weightEntryRepository = weightEntryRepository;
        this.exerciseEntryRepository = exerciseEntryRepository;
    }

    @GetMapping("/weights/recent")
    public List<WeightEntryResponse> recentWeights(
            @RequestParam(name = "limit", defaultValue = "60") int limit) {
        return weightEntryRepository.findAllByOrderByLoggedOnDesc().stream()
                .limit(Math.min(limit, 500))
                .map(WeightEntryResponse::from)
                .toList();
    }

    @PostMapping("/weights")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightEntryResponse saveWeight(@Valid @RequestBody WeightRequest request) {
        WeightEntry entry = new WeightEntry();
        entry.setLoggedOn(request.getLoggedOn());
        entry.setWeightKg(request.getWeightKg());
        entry.setNote(trimToNull(request.getNote()));

        return weightEntryRepository.findByLoggedOn(request.getLoggedOn()).map(existing -> {
            existing.setWeightKg(entry.getWeightKg());
            existing.setNote(entry.getNote());
            return WeightEntryResponse.from(weightEntryRepository.save(existing));
        }).orElseGet(() -> WeightEntryResponse.from(weightEntryRepository.save(entry)));
    }

    @GetMapping("/activities/recent")
    public List<ExerciseEntryResponse> recentActivities(
            @RequestParam(name = "limit", defaultValue = "60") int limit) {
        return exerciseEntryRepository.findAllByOrderByLoggedOnDescIdDesc().stream()
                .limit(Math.min(limit, 500))
                .map(ExerciseEntryResponse::from)
                .toList();
    }

    @PostMapping("/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseEntryResponse saveActivity(@Valid @RequestBody ExerciseRequest request) {
        ExerciseEntry entry = new ExerciseEntry();
        entry.setLoggedOn(request.getLoggedOn());
        entry.setActivity(request.getActivity().trim());
        entry.setDurationMinutes(request.getDurationMinutes());
        entry.setNote(trimToNull(request.getNote()));
        return ExerciseEntryResponse.from(exerciseEntryRepository.save(entry));
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
