package com.spbmi.tracker.common.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModulesController {

    private static final List<ModuleInfo> MODULES = List.of(
            new ModuleInfo("exercise", "Exercise", "Weight and activity logging", true),
            new ModuleInfo("finance", "Finance", "Money and budgets", false),
            new ModuleInfo("work", "Work", "Tasks and time at work", false),
            new ModuleInfo("learning", "Learning", "Courses and study time", false),
            new ModuleInfo("gardening", "Gardening", "Plants and outdoor work", false),
            new ModuleInfo("travel", "Travel", "Trips and mileage", false)
    );

    @GetMapping
    public List<ModuleInfo> list() {
        return MODULES;
    }
}
