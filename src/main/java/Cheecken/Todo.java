package Cheecken;

/** Represents a task without a deadline or event time. */
public class Todo extends Task {
    public Todo(String task) {
        super(task);
    }

    public String toString() {
        return String.format("[T]" + super.toString());
    }
    @Override
    public String toStorageString() {
        return "T | " + (isMarked ? "1" : "0") + " | " + task;
    }
}
    /** Creates a todo task. */
    /** Returns the todo display representation. */
    /** Serializes the todo task. */
