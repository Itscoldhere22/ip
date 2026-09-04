import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    protected LocalDate deadline;

    public Deadline(String task, String deadline) {
        super(task);
        this.deadline = parseDate(deadline);
    }

    private static LocalDate parseDate(String value) {
        try { return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException ignored) { }
        try { return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("d/M/yyyy HHmm")).toLocalDate(); }
        catch (DateTimeParseException ignored) { }
        throw new IllegalArgumentException("Invalid deadline date. Use yyyy-MM-dd or dd/MM/yyyy HHmm.");
    }

    public String toString() {
        return String.format("[D]" + super.toString() + " (by: "
                + deadline.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")");
    }
    @Override
    public String toStorageString() {
        return "D | " + (marked ? "1" : "0") + " | " + task + " | " + deadline;
    }
}
