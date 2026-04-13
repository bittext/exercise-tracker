package com.spbmi.tracker.exercise.calendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class CalendarGrid {

    private CalendarGrid() {
    }

    /** Sunday-first month grid; empty cells are {@code null}. */
    public static List<List<LocalDate>> buildMonthWeeks(YearMonth yearMonth) {
        LocalDate first = yearMonth.atDay(1);
        int leadingBlanks = first.getDayOfWeek().getValue() % 7;
        int daysInMonth = yearMonth.lengthOfMonth();

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> row = new ArrayList<>();
        for (int i = 0; i < leadingBlanks; i++) {
            row.add(null);
        }
        for (int day = 1; day <= daysInMonth; day++) {
            row.add(yearMonth.atDay(day));
            if (row.size() == 7) {
                weeks.add(row);
                row = new ArrayList<>();
            }
        }
        while (!row.isEmpty() && row.size() < 7) {
            row.add(null);
        }
        if (!row.isEmpty()) {
            weeks.add(row);
        }
        return weeks;
    }
}
