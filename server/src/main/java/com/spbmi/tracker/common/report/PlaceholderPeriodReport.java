package com.spbmi.tracker.common.report;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/**
 * Standard JSON shape for daily / monthly reports on modules that are not yet backed by domain data.
 */
public record PlaceholderPeriodReport(
        String moduleId,
        String period,
        LocalDate date,
        String yearMonth,
        boolean implemented,
        String message,
        Map<String, Object> summary
) {
    public static PlaceholderPeriodReport daily(String moduleId, LocalDate date) {
        return new PlaceholderPeriodReport(
                moduleId,
                "DAILY",
                date,
                null,
                false,
                "This area is reserved; connect your data model here.",
                Map.of()
        );
    }

    public static PlaceholderPeriodReport monthly(String moduleId, YearMonth ym) {
        return new PlaceholderPeriodReport(
                moduleId,
                "MONTHLY",
                null,
                ym.toString(),
                false,
                "This area is reserved; connect your data model here.",
                Map.of()
        );
    }
}
