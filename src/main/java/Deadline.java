import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Deadline extends Task {
    protected LocalDateTime deadline;
    private final boolean hasTime;

    public Deadline(String task, String deadline) {
        super(task);
        this.hasTime = deadline.trim().contains(" ") || deadline.contains("T");
        this.deadline = parseDateTime(deadline);
    }

    private static LocalDateTime parseDateTime(String value) {
        try { return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
        catch (DateTimeParseException ignored) { }
        try { return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy HHmm")); }
        catch (DateTimeParseException ignored) { }
        try { return LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy")).atStartOfDay(); }
        catch (DateTimeParseException ignored) { }
        try { return LocalDateTime.parse(value + "T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
        catch (DateTimeParseException ignored) { }
        throw new CheeckenDateTimeException("deadline");
    }

    public String toString() {
        String pattern = hasTime ? "MMM dd yyyy h:mm a" : "MMM dd yyyy";
        return "[D]" + super.toString() + " (by: "
                + deadline.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)) + ")";
    }

    @Override
    public String toStorageString() {
        return "D | " + (marked ? "1" : "0") + " | " + task + " | " + deadline;
    }
}
