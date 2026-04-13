package com.spbmi.tracker.stub;

import com.spbmi.tracker.common.report.PlaceholderPeriodReport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Daily and monthly report placeholders for domains without persistence yet.
 */
@RestController
@RequestMapping
public class StubDomainReportsRestController {

    @GetMapping("/api/finance/reports/daily")
    public PlaceholderPeriodReport financeDaily(@RequestParam(required = false) LocalDate date) {
        return PlaceholderPeriodReport.daily("finance", date != null ? date : LocalDate.now());
    }

    @GetMapping("/api/finance/reports/monthly")
    public PlaceholderPeriodReport financeMonthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return PlaceholderPeriodReport.monthly("finance", resolveYm(year, month));
    }

    @GetMapping("/api/work/reports/daily")
    public PlaceholderPeriodReport workDaily(@RequestParam(required = false) LocalDate date) {
        return PlaceholderPeriodReport.daily("work", date != null ? date : LocalDate.now());
    }

    @GetMapping("/api/work/reports/monthly")
    public PlaceholderPeriodReport workMonthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return PlaceholderPeriodReport.monthly("work", resolveYm(year, month));
    }

    @GetMapping("/api/learning/reports/daily")
    public PlaceholderPeriodReport learningDaily(@RequestParam(required = false) LocalDate date) {
        return PlaceholderPeriodReport.daily("learning", date != null ? date : LocalDate.now());
    }

    @GetMapping("/api/learning/reports/monthly")
    public PlaceholderPeriodReport learningMonthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return PlaceholderPeriodReport.monthly("learning", resolveYm(year, month));
    }

    @GetMapping("/api/gardening/reports/daily")
    public PlaceholderPeriodReport gardeningDaily(@RequestParam(required = false) LocalDate date) {
        return PlaceholderPeriodReport.daily("gardening", date != null ? date : LocalDate.now());
    }

    @GetMapping("/api/gardening/reports/monthly")
    public PlaceholderPeriodReport gardeningMonthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return PlaceholderPeriodReport.monthly("gardening", resolveYm(year, month));
    }

    @GetMapping("/api/travel/reports/daily")
    public PlaceholderPeriodReport travelDaily(@RequestParam(required = false) LocalDate date) {
        return PlaceholderPeriodReport.daily("travel", date != null ? date : LocalDate.now());
    }

    @GetMapping("/api/travel/reports/monthly")
    public PlaceholderPeriodReport travelMonthly(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return PlaceholderPeriodReport.monthly("travel", resolveYm(year, month));
    }

    private static YearMonth resolveYm(Integer year, Integer month) {
        if (year != null && month != null && month >= 1 && month <= 12) {
            return YearMonth.of(year, month);
        }
        return YearMonth.now();
    }
}
