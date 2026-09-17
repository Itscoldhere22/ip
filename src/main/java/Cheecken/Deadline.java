package Cheecken;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents a task that must be completed by a date or date-time. */
public class Deadline extends Task {
    protected LocalDateTime deadline;
    private final boolean hasExplicitTime;

    public Deadline(String task, String deadline) {
        super(task);
        this.hasExplicitTime = deadline.trim().contains(" ") || deadline.contains("T");
        this.deadline = parseDateTime(deadline);
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy")).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(value + "T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Fall through to the user-facing exception.
        }
        throw new CheeckenDateTimeException("deadline");
    }

    public String toString() {
        String pattern = hasExplicitTime ? "MMM dd yyyy h:mm a" : "MMM dd yyyy";
        return "[D]" + super.toString() + " (by: "
                + deadline.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)) + ")";
    }

    @Override
    public String toStorageString() {
        return "D | " + (isMarked ? "1" : "0") + " | " + task + " | " + deadline;
    }
}
    /** Creates a deadline after parsing its date or date-time. */
    /** Returns the deadline display representation. */
    /** Serializes the deadline task. */
