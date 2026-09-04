public class Event extends Task {
    protected String startTime;
    protected String endTime;

    public Event(String task, String startTime, String endTime) {
        super(task);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString() {
        return String.format("[E]" + super.toString() +
                " (from: " + this.startTime + " to: " + this.endTime + ")");
    }
    @Override
    public String toStorageString() {
        return "E | " + (marked ? "1" : "0") + " | " + task + " | " + startTime + " | " + endTime;
    }
}
