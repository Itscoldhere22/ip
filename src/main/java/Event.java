import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task {
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Event(String task, String startTime, String endTime) {
        super(task);
        this.startTime = parseDateTime(startTime);
        this.endTime = parseDateTime(endTime);
    }

    private static LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm") }) {
            try { return LocalDateTime.parse(value, formatter); }
            catch (DateTimeParseException ignored) { }
        }
        throw new IllegalArgumentException("Invalid event date/time. Use yyyy-MM-ddTHH:mm or dd/MM/yyyy HHmm.");
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy HHmm");
        return String.format("[E]" + super.toString() +
                " (from: " + startTime.format(formatter) + " to: " + endTime.format(formatter) + ")");
    }
    @Override
    public String toStorageString() {
        return "E | " + (marked ? "1" : "0") + " | " + task + " | " + startTime + " | " + endTime;
    }
}
