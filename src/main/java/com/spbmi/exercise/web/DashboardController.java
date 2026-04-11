package com.spbmi.exercise.web;

import com.spbmi.exercise.model.ExerciseEntry;
import com.spbmi.exercise.model.WeightEntry;
import com.spbmi.exercise.repo.ExerciseEntryRepository;
import com.spbmi.exercise.repo.WeightEntryRepository;
import com.spbmi.exercise.web.dto.ExerciseForm;
import com.spbmi.exercise.web.dto.WeightForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DashboardController {

    private static final int RECENT_LIMIT = 60;

    private final WeightEntryRepository weightEntryRepository;
    private final ExerciseEntryRepository exerciseEntryRepository;

    public DashboardController(
            WeightEntryRepository weightEntryRepository,
            ExerciseEntryRepository exerciseEntryRepository) {
        this.weightEntryRepository = weightEntryRepository;
        this.exerciseEntryRepository = exerciseEntryRepository;
    }

    @GetMapping("/")
    public String dashboard(
            Model model,
            @RequestParam(value = "weightSaved", required = false) Boolean weightSaved,
            @RequestParam(value = "exerciseSaved", required = false) Boolean exerciseSaved) {
        List<WeightEntry> weights = weightEntryRepository.findAllByOrderByLoggedOnDesc().stream()
                .limit(RECENT_LIMIT)
                .toList();
        List<ExerciseEntry> exercises = exerciseEntryRepository.findAllByOrderByLoggedOnDescIdDesc().stream()
                .limit(RECENT_LIMIT)
                .toList();

        if (!model.containsAttribute("weightForm")) {
            model.addAttribute("weightForm", new WeightForm());
        }
        if (!model.containsAttribute("exerciseForm")) {
            model.addAttribute("exerciseForm", new ExerciseForm());
        }

        model.addAttribute("weights", weights);
        model.addAttribute("exercises", exercises);
        model.addAttribute("weightSaved", Boolean.TRUE.equals(weightSaved));
        model.addAttribute("exerciseSaved", Boolean.TRUE.equals(exerciseSaved));
        return "dashboard";
    }

    @PostMapping("/weights")
    public String addWeight(
            @Valid WeightForm weightForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("weightForm", weightForm);
            model.addAttribute("exerciseForm", new ExerciseForm());
            addListsForRedisplay(model);
            return "dashboard";
        }

        WeightEntry entry = new WeightEntry();
        entry.setLoggedOn(weightForm.getLoggedOn());
        entry.setWeightKg(weightForm.getWeightKg());
        entry.setNote(trimToNull(weightForm.getNote()));

        weightEntryRepository.findByLoggedOn(weightForm.getLoggedOn()).ifPresentOrElse(
                existing -> {
                    existing.setWeightKg(entry.getWeightKg());
                    existing.setNote(entry.getNote());
                    weightEntryRepository.save(existing);
                },
                () -> weightEntryRepository.save(entry));

        redirectAttributes.addAttribute("weightSaved", true);
        return "redirect:/";
    }

    @PostMapping("/exercises")
    public String addExercise(
            @Valid ExerciseForm exerciseForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("exerciseForm", exerciseForm);
            model.addAttribute("weightForm", new WeightForm());
            addListsForRedisplay(model);
            return "dashboard";
        }

        ExerciseEntry entry = new ExerciseEntry();
        entry.setLoggedOn(exerciseForm.getLoggedOn());
        entry.setActivity(exerciseForm.getActivity().trim());
        entry.setDurationMinutes(exerciseForm.getDurationMinutes());
        entry.setNote(trimToNull(exerciseForm.getNote()));
        exerciseEntryRepository.save(entry);

        redirectAttributes.addAttribute("exerciseSaved", true);
        return "redirect:/";
    }

    private void addListsForRedisplay(Model model) {
        model.addAttribute("weights", weightEntryRepository.findAllByOrderByLoggedOnDesc().stream()
                .limit(RECENT_LIMIT)
                .toList());
        model.addAttribute("exercises", exerciseEntryRepository.findAllByOrderByLoggedOnDescIdDesc().stream()
                .limit(RECENT_LIMIT)
                .toList());
        model.addAttribute("weightSaved", false);
        model.addAttribute("exerciseSaved", false);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
