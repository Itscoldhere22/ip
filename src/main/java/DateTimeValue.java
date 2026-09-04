import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Shared parsed date/time value for deadlines and events. */
public record DateTimeValue(LocalDateTime value, boolean hasExplicitTime) {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);
    public static DateTimeValue parse(String text, String command) {
        return parse(text, command, Clock.systemDefaultZone());
    }

    public static DateTimeValue parse(String text, String command, Clock clock) {
        String value = text.trim();
        if (value.equalsIgnoreCase("today")) {
            return new DateTimeValue(LocalDate.now(clock).atStartOfDay(), false);
        }
        if (value.equalsIgnoreCase("now")) {
            return new DateTimeValue(LocalDateTime.now(clock), true);
        }
        try { return new DateTimeValue(LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), true); } catch (DateTimeParseException ignored) { }
        try { return new DateTimeValue(LocalDateTime.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy HHmm")), true); } catch (DateTimeParseException ignored) { }
        try { return new DateTimeValue(LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy")).atStartOfDay(), false); } catch (DateTimeParseException ignored) { }
        try { return new DateTimeValue(LocalDate.parse(value).atStartOfDay(), false); } catch (DateTimeParseException ignored) { }
        throw new CheeckenDateTimeException(command);
    }
    public String display() { return value.format(hasExplicitTime ? TIME : DATE); }
    public String storage() { return hasExplicitTime ? value.toString() : value.toLocalDate().toString(); }
}
