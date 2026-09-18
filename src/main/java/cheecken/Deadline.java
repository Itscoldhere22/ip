package cheecken;

/**
 * Represents a task that must be completed by a date or date-time.
 */
public class Deadline extends Task {
    private final DateTimeValue deadline;

    /**
     * Creates a deadline after parsing its date or date-time using the local clock.
     */
    public Deadline(String task, String deadline) {
        this(task, DateTimeValue.parse(deadline, "deadline"));
    }

    /**
     * Creates a deadline from a value already resolved by command handling or storage.
     */
    Deadline(String task, DateTimeValue deadline) {
        super(task);
        this.deadline = deadline;
    }

    /**
     * Returns the deadline display representation.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline.display() + ")";
    }

    /**
     * Serializes the resolved deadline, preserving whether a time was supplied.
     */
    @Override
    public String toStorageString() {
        return "D | " + (isMarked ? "1" : "0") + " | " + task + " | " + deadline.storage();
    }
}
