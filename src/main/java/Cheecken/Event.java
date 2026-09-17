package Cheecken;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Event extends Task {
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    private final boolean hasStartTime;
    private final boolean hasEndTime;

    public Event(String task, String startTime, String endTime) {
        super(task);
        this.hasStartTime = hasTime(startTime);
        this.hasEndTime = hasTime(endTime);
        this.startTime = parseDateTime(startTime);
        this.endTime = parseDateTime(endTime);
    }

    private static boolean hasTime(String value) {
        return value.trim().contains(" ") || value.contains("T");
    }

    private static LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm") }) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy")).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Try ISO date format next.
        }
        try {
            return LocalDate.parse(value).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Fall through to the user-facing exception.
        }
        throw new CheeckenDateTimeException("event");
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);
        String start = hasStartTime ? startTime.format(formatter)
                : startTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        String end = hasEndTime ? endTime.format(formatter)
                : endTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return String.format("[E]" + super.toString() +
                " (from: " + start + " to: " + end + ")");
    }
    @Override
    public String toStorageString() {
        return "E | " + (isMarked ? "1" : "0") + " | " + task + " | "
                + (hasStartTime ? startTime : startTime.toLocalDate()) + " | "
                + (hasEndTime ? endTime : endTime.toLocalDate());
    }
}
