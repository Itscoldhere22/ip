package cheecken;

import java.time.Clock;

/**
 * Represents a task with a start and end date or date-time.
 */
public class Event extends Task {
    private final DateTimeValue startTime;
    private final DateTimeValue endTime;

    /**
     * Creates an event using one local clock reading for both endpoints.
     */
    public Event(String task, String startTime, String endTime) {
        this(task, startTime, endTime, Clock.systemDefaultZone());
    }

    /**
     * Resolves both event endpoints independently against the supplied clock.
     */
    Event(String task, String startTime, String endTime, Clock clock) {
        super(task);
        Clock reference = Clock.fixed(clock.instant(), clock.getZone());
        this.startTime = DateTimeValue.parse(startTime, "event", reference);
        this.endTime = DateTimeValue.parse(endTime, "event", reference);
    }

    /**
     * Restores already resolved endpoints without reinterpreting saved dates.
     */
    Event(String task, DateTimeValue startTime, DateTimeValue endTime) {
        super(task);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event display representation.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startTime.display() + " to: " + endTime.display() + ")";
    }

    /**
     * Serializes the resolved endpoints, preserving whether each time was supplied.
     */
    @Override
    public String toStorageString() {
        return "E | " + (isMarked ? "1" : "0") + " | " + task + " | "
                + startTime.storage() + " | " + endTime.storage();
    }
}
