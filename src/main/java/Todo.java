public class Todo extends Task {
    public Todo(String task) {
        super(task);
    }

    public String toString() {
        return String.format("[T]" + super.toString());
    }
    @Override
    public String toStorageString() {
        return "T | " + (marked ? "1" : "0") + " | " + task;
    }
}
