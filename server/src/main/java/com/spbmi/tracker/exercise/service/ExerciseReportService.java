package com.spbmi.tracker.exercise.service;

import com.spbmi.tracker.exercise.api.dto.CalendarCellDto;
import com.spbmi.tracker.exercise.api.dto.ExerciseDailyReportDto;
import com.spbmi.tracker.exercise.api.dto.ExerciseEntryResponse;
import com.spbmi.tracker.exercise.api.dto.ExerciseMonthlyReportDto;
import com.spbmi.tracker.exercise.api.dto.WeightEntryResponse;
import com.spbmi.tracker.exercise.calendar.CalendarGrid;
import com.spbmi.tracker.exercise.model.ExerciseEntry;
import com.spbmi.tracker.exercise.model.WeightEntry;
import com.spbmi.tracker.exercise.repo.ExerciseEntryRepository;
import com.spbmi.tracker.exercise.repo.WeightEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExerciseReportService {

    private final WeightEntryRepository weightEntryRepository;
    private final ExerciseEntryRepository exerciseEntryRepository;

    public ExerciseReportService(
            WeightEntryRepository weightEntryRepository,
            ExerciseEntryRepository exerciseEntryRepository) {
        this.weightEntryRepository = weightEntryRepository;
        this.exerciseEntryRepository = exerciseEntryRepository;
    }

    public ExerciseDailyReportDto buildDaily(LocalDate day) {
        List<ExerciseEntry> exercises = exerciseEntryRepository.findByLoggedOnOrderByIdAsc(day);
        Map<String, Integer> byActivity = new LinkedHashMap<>();
        for (ExerciseEntry e : exercises) {
            byActivity.merge(e.getActivity(), e.getDurationMinutes(), Integer::sum);
        }
        int total = exercises.stream().mapToInt(ExerciseEntry::getDurationMinutes).sum();
        Optional<WeightEntry> w = weightEntryRepository.findByLoggedOn(day);
        return new ExerciseDailyReportDto(
                day,
                w.map(WeightEntryResponse::from).orElse(null),
                exercises.stream().map(ExerciseEntryResponse::from).toList(),
                exercises.size(),
                total,
                byActivity
        );
    }

    public ExerciseMonthlyReportDto buildMonthly(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        LocalDate today = LocalDate.now();

        List<ExerciseEntry> monthExercises =
                exerciseEntryRepository.findByLoggedOnBetweenOrderByLoggedOnAscIdAsc(start, end);
        Set<LocalDate> exerciseDates = monthExercises.stream()
                .map(ExerciseEntry::getLoggedOn)
                .collect(Collectors.toCollection(HashSet::new));

        Map<LocalDate, Integer> minutesByDate = new HashMap<>();
        for (ExerciseEntry e : monthExercises) {
            minutesByDate.merge(e.getLoggedOn(), e.getDurationMinutes(), Integer::sum);
        }

        List<WeightEntry> monthWeights = weightEntryRepository.findByLoggedOnBetweenOrderByLoggedOnAsc(start, end);
        Map<String, BigDecimal> weightKgByIso = new HashMap<>();
        for (WeightEntry w : monthWeights) {
            weightKgByIso.put(w.getLoggedOn().toString(), w.getWeightKg());
        }
        Map<String, Integer> exerciseMinutesByIso = new HashMap<>();
        for (Map.Entry<LocalDate, Integer> e : minutesByDate.entrySet()) {
            exerciseMinutesByIso.put(e.getKey().toString(), e.getValue());
        }

        List<List<CalendarCellDto>> weeks = new ArrayList<>();
        for (List<LocalDate> row : CalendarGrid.buildMonthWeeks(ym)) {
            List<CalendarCellDto> out = new ArrayList<>();
            for (LocalDate cell : row) {
                out.add(toCell(cell, today, exerciseDates, minutesByDate, weightKgByIso));
            }
            weeks.add(out);
        }

        BigDecimal min = null;
        BigDecimal max = null;
        BigDecimal avg = null;
        if (!monthWeights.isEmpty()) {
            List<BigDecimal> sorted = monthWeights.stream()
                    .map(WeightEntry::getWeightKg)
                    .sorted()
                    .toList();
            BigDecimal sum = sorted.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            min = sorted.getFirst();
            max = sorted.getLast();
            avg = sum.divide(BigDecimal.valueOf(sorted.size()), 2, RoundingMode.HALF_UP);
        }

        Map<String, Integer> activityMinutes = new LinkedHashMap<>();
        monthExercises.stream()
                .collect(Collectors.groupingBy(ExerciseEntry::getActivity, Collectors.summingInt(ExerciseEntry::getDurationMinutes)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> activityMinutes.put(e.getKey(), e.getValue()));

        List<WeightEntryResponse> weightRows = monthWeights.stream()
                .sorted(Comparator.comparing(WeightEntry::getLoggedOn))
                .map(WeightEntryResponse::from)
                .toList();

        Set<String> exerciseDatesIso = exerciseDates.stream().map(LocalDate::toString).collect(Collectors.toSet());

        Map<String, String> weightDisplay = new HashMap<>();
        for (Map.Entry<String, BigDecimal> e : weightKgByIso.entrySet()) {
            weightDisplay.put(e.getKey(), e.getValue().toPlainString());
        }

        return new ExerciseMonthlyReportDto(
                year,
                month,
                today,
                weeks,
                exerciseDatesIso,
                exerciseMinutesByIso,
                weightDisplay,
                exerciseDates.size(),
                monthExercises.size(),
                monthExercises.stream().mapToInt(ExerciseEntry::getDurationMinutes).sum(),
                monthWeights.size(),
                min,
                max,
                avg,
                activityMinutes,
                weightRows
        );
    }

    private static CalendarCellDto toCell(
            LocalDate cell,
            LocalDate today,
            Set<LocalDate> exerciseDates,
            Map<LocalDate, Integer> minutesByDate,
            Map<String, BigDecimal> weightKgByIso) {
        if (cell == null) {
            return new CalendarCellDto(true, null, null, false, false, null, null);
        }
        String iso = cell.toString();
        Integer mins = minutesByDate.get(cell);
        BigDecimal w = weightKgByIso.get(iso);
        return new CalendarCellDto(
                false,
                iso,
                cell.getDayOfMonth(),
                cell.equals(today),
                exerciseDates.contains(cell),
                mins,
                w != null ? w.toPlainString() : null
        );
    }
}
