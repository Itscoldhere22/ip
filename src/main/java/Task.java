public class Task {
    protected final String task;
    protected boolean marked;

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

    public String toStorageString() {
        return "T | " + (marked ? "1" : "0") + " | " + task;
    }

    @Override
    public String toString() {
        return String.format("[" + (this.marked ? "X" : " ") + "] " + this.task);
    }
}
