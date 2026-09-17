public class Task {
    protected final String task;
    protected boolean isMarked;

    public Task(String task) {
        this.task = task;
        this.isMarked = false;
    }

    public void mark() {
        this.isMarked = true;
    }

    public void unmark() {
        this.isMarked = false;
    }

    public String toStorageString() {
        return "T | " + (isMarked ? "1" : "0") + " | " + task;
    }

    @Override
    public String toString() {
        return String.format("[" + (this.isMarked ? "X" : " ") + "] " + this.task);
    }
}
