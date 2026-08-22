public class Task {
    private final String task;
    private boolean marked;

    public Task(String task) {
        this.task = task;
        this.marked = false;
    }

    public void mark() {
        this.marked = true;
    }

    public void unmark() {
        this.marked = false;
    }

    @Override
    public String toString() {
        return String.format("[" + (this.marked ? "X" : " ") + "] " + this.task);
    }
}
