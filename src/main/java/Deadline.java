public class Deadline extends Task {
    protected String deadline;

    public Deadline(String task, String deadline) {
        super(task);
        this.deadline = deadline;
    }

    public String toString() {
        return String.format("[D]" + super.toString() + " (by: " + this.deadline + ")");
    }
}