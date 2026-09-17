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

    /** Returns tasks whose descriptions contain the keyword, in list order. */
    public List<Task> find(String keyword) {
        return tasks.stream().filter(task -> task.containsKeyword(keyword)).toList();
    }
}
