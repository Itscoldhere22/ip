package cheecken;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/**
 * Parsed date-time value that preserves whether a time was explicitly entered.
 */
public record DateTimeValue(LocalDateTime value, boolean hasExplicitTime) {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter NATURAL_TIME = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses supported keywords and date formats using the system clock.
     */
    public static DateTimeValue parse(String text, String command) {
        return parse(text, command, Clock.systemDefaultZone());
    }

    /**
     * Parses supported keywords and date formats using a supplied clock.
     */
    public static DateTimeValue parse(String text, String command, Clock clock) {
        String value = text.trim();
        if (value.equalsIgnoreCase("now")) {
            return new DateTimeValue(LocalDateTime.now(clock), true);
        }
        String[] parts = value.split("\\s+", 2);
        LocalDate date = resolveNaturalDate(parts[0], LocalDate.now(clock));
        if (date == null) {
            return parseAbsolute(value, command);
        }
        if (parts.length == 1) {
            return new DateTimeValue(date.atStartOfDay(), false);
        }
        try {
            return new DateTimeValue(date.atTime(LocalTime.parse(parts[1], NATURAL_TIME)), true);
        } catch (DateTimeParseException e) {
            throw new CheeckenDateTimeException(command);
        }
    }

    /**
     * Resolves supported English date words; weekdays always advance at least one day.
     */
    private static LocalDate resolveNaturalDate(String word, LocalDate today) {
        if (word.equalsIgnoreCase("today")) {
            return today;
        }
        if (word.equalsIgnoreCase("tomorrow")) {
            return today.plusDays(1);
        }
        for (DayOfWeek day : DayOfWeek.values()) {
            if (word.equalsIgnoreCase(day.name())
                    || word.equalsIgnoreCase(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))) {
                return today.with(TemporalAdjusters.next(day));
            }
        }
        return null;
    }

    /**
     * Parses existing absolute formats without interpreting relative words in saved records.
     */
    static DateTimeValue parseAbsolute(String text, String command) {
        String value = text.trim();
        try {
            return new DateTimeValue(LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return new DateTimeValue(LocalDateTime.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy HHmm")), true);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return new DateTimeValue(
                    LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy")).atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return new DateTimeValue(LocalDate.parse(value).atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // Fall through to the user-facing exception.
        }
        throw new CheeckenDateTimeException(command);
    }

    /**
     * Formats this value for user-facing output.
     */
    public String display() {
        return value.format(hasExplicitTime ? TIME : DATE);
    }

    /**
     * Formats this value for persistence.
     */
    public String storage() {
        return hasExplicitTime ? value.toString() : value.toLocalDate().toString();
    }
}
