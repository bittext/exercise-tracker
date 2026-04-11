package com.spbmi.exercise.web;

import com.spbmi.exercise.model.ExerciseEntry;
import com.spbmi.exercise.model.WeightEntry;
import com.spbmi.exercise.repo.ExerciseEntryRepository;
import com.spbmi.exercise.repo.WeightEntryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ReportsController {

    private final WeightEntryRepository weightEntryRepository;
    private final ExerciseEntryRepository exerciseEntryRepository;

    public ReportsController(
            WeightEntryRepository weightEntryRepository,
            ExerciseEntryRepository exerciseEntryRepository) {
        this.weightEntryRepository = weightEntryRepository;
        this.exerciseEntryRepository = exerciseEntryRepository;
    }

    @GetMapping("/reports")
    public String reports(
            @RequestParam(name = "mode", defaultValue = "monthly") String mode,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month,
            Model model) {

        boolean daily = "daily".equalsIgnoreCase(mode);
        model.addAttribute("mode", daily ? "daily" : "monthly");
        model.addAttribute("today", LocalDate.now());

        if (daily) {
            LocalDate day = date != null ? date : LocalDate.now();
            model.addAttribute("selectedDate", day);
            model.addAttribute("prevDay", day.minusDays(1));
            model.addAttribute("nextDay", day.plusDays(1));

            List<ExerciseEntry> dayExercises = exerciseEntryRepository.findByLoggedOnOrderByIdAsc(day);
            model.addAttribute("dayExercises", dayExercises);
            int totalMinutes = dayExercises.stream().mapToInt(ExerciseEntry::getDurationMinutes).sum();
            model.addAttribute("dayExerciseSessions", dayExercises.size());
            model.addAttribute("dayExerciseMinutes", totalMinutes);

            Optional<WeightEntry> weight = weightEntryRepository.findByLoggedOn(day);
            model.addAttribute("dayWeight", weight.orElse(null));

            Map<String, Integer> minutesByActivity = new LinkedHashMap<>();
            for (ExerciseEntry e : dayExercises) {
                minutesByActivity.merge(e.getActivity(), e.getDurationMinutes(), Integer::sum);
            }
            model.addAttribute("dayMinutesByActivity", minutesByActivity);

            model.addAttribute("calendarWeeks", List.<List<LocalDate>>of());
            model.addAttribute("calendarYearMonth", YearMonth.from(day));
            model.addAttribute("exerciseDatesInView", Set.<LocalDate>of());
            model.addAttribute("minutesByDateInView", Map.<LocalDate, Integer>of());
            model.addAttribute("weightByDateInView", Map.<LocalDate, WeightEntry>of());
            model.addAttribute("weightKgByIso", Map.<String, BigDecimal>of());
            model.addAttribute("exerciseMinutesByIso", Map.<String, Integer>of());
            model.addAttribute("monthlyExerciseDays", 0);
            model.addAttribute("monthlyExerciseSessions", 0);
            model.addAttribute("monthlyExerciseMinutes", 0);
            model.addAttribute("monthlyWeightsCount", 0);
            model.addAttribute("monthWeightMin", Optional.<BigDecimal>empty());
            model.addAttribute("monthWeightMax", Optional.<BigDecimal>empty());
            model.addAttribute("monthWeightAvg", Optional.<BigDecimal>empty());
            model.addAttribute("monthMinutesByActivity", Map.<String, Integer>of());
            model.addAttribute("monthWeightRows", List.<WeightEntry>of());
            model.addAttribute("prevYearMonth", YearMonth.from(day.minusMonths(1)));
            model.addAttribute("nextYearMonth", YearMonth.from(day.plusMonths(1)));
            return "reports";
        }

        YearMonth ym = resolveYearMonth(year, month);
        model.addAttribute("calendarYearMonth", ym);
        model.addAttribute("prevYearMonth", ym.minusMonths(1));
        model.addAttribute("nextYearMonth", ym.plusMonths(1));

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<ExerciseEntry> monthExercises = exerciseEntryRepository.findByLoggedOnBetweenOrderByLoggedOnAscIdAsc(start, end);
        Set<LocalDate> exerciseDates = monthExercises.stream()
                .map(ExerciseEntry::getLoggedOn)
                .collect(Collectors.toCollection(HashSet::new));

        Map<LocalDate, Integer> minutesByDate = new HashMap<>();
        for (ExerciseEntry e : monthExercises) {
            minutesByDate.merge(e.getLoggedOn(), e.getDurationMinutes(), Integer::sum);
        }

        List<WeightEntry> monthWeights = weightEntryRepository.findByLoggedOnBetweenOrderByLoggedOnAsc(start, end);
        Map<LocalDate, WeightEntry> weightByDate = monthWeights.stream()
                .collect(Collectors.toMap(WeightEntry::getLoggedOn, w -> w, (a, b) -> a));

        Map<String, BigDecimal> weightKgByIso = new HashMap<>();
        for (WeightEntry w : monthWeights) {
            weightKgByIso.put(w.getLoggedOn().toString(), w.getWeightKg());
        }
        Map<String, Integer> exerciseMinutesByIso = new HashMap<>();
        for (Map.Entry<LocalDate, Integer> e : minutesByDate.entrySet()) {
            exerciseMinutesByIso.put(e.getKey().toString(), e.getValue());
        }

        model.addAttribute("calendarWeeks", CalendarGrid.buildMonthWeeks(ym));
        model.addAttribute("exerciseDatesInView", exerciseDates);
        model.addAttribute("minutesByDateInView", minutesByDate);
        model.addAttribute("weightByDateInView", weightByDate);
        model.addAttribute("weightKgByIso", weightKgByIso);
        model.addAttribute("exerciseMinutesByIso", exerciseMinutesByIso);

        model.addAttribute("monthlyExerciseDays", exerciseDates.size());
        model.addAttribute("monthlyExerciseSessions", monthExercises.size());
        model.addAttribute("monthlyExerciseMinutes", monthExercises.stream().mapToInt(ExerciseEntry::getDurationMinutes).sum());

        model.addAttribute("monthlyWeightsCount", monthWeights.size());
        if (monthWeights.isEmpty()) {
            model.addAttribute("monthWeightMin", Optional.<BigDecimal>empty());
            model.addAttribute("monthWeightMax", Optional.<BigDecimal>empty());
            model.addAttribute("monthWeightAvg", Optional.<BigDecimal>empty());
        } else {
            List<BigDecimal> sorted = monthWeights.stream()
                    .map(WeightEntry::getWeightKg)
                    .sorted()
                    .toList();
            BigDecimal sum = sorted.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(BigDecimal.valueOf(sorted.size()), 2, RoundingMode.HALF_UP);
            model.addAttribute("monthWeightMin", Optional.of(sorted.getFirst()));
            model.addAttribute("monthWeightMax", Optional.of(sorted.getLast()));
            model.addAttribute("monthWeightAvg", Optional.of(avg));
        }

        Map<String, Integer> activityMinutes = new LinkedHashMap<>();
        monthExercises.stream()
                .collect(Collectors.groupingBy(ExerciseEntry::getActivity, Collectors.summingInt(ExerciseEntry::getDurationMinutes)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> activityMinutes.put(e.getKey(), e.getValue()));
        model.addAttribute("monthMinutesByActivity", activityMinutes);

        List<WeightEntry> monthWeightRows = monthWeights.stream()
                .sorted(Comparator.comparing(WeightEntry::getLoggedOn))
                .toList();
        model.addAttribute("monthWeightRows", monthWeightRows);

        LocalDate today = LocalDate.now();
        model.addAttribute("selectedDate", today);
        model.addAttribute("prevDay", today.minusDays(1));
        model.addAttribute("nextDay", today.plusDays(1));
        model.addAttribute("dayExercises", List.<ExerciseEntry>of());
        model.addAttribute("dayExerciseSessions", 0);
        model.addAttribute("dayExerciseMinutes", 0);
        model.addAttribute("dayWeight", null);
        model.addAttribute("dayMinutesByActivity", Map.<String, Integer>of());

        return "reports";
    }

    private static YearMonth resolveYearMonth(Integer year, Integer month) {
        if (year != null && month != null && month >= 1 && month <= 12) {
            return YearMonth.of(year, month);
        }
        return YearMonth.now();
    }
}
