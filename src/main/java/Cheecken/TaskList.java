package Cheecken;

import java.util.ArrayList;
import java.util.List;

/** Owns the chatbot's tasks and exposes task-list operations. */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
    /** Creates an empty task list. */
    /** Adds a task to the list. */
    /** Returns the task at a zero-based index. */
    /** Removes and returns the task at a zero-based index. */
    /** Returns the number of tasks. */
    /** Returns an immutable snapshot of the tasks. */
