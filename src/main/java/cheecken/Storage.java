package cheecken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Loads and saves task records in the application's persistence file.
 */
public class Storage {
    private final Path file;
    private final Consumer<String> reportError;

    /**
     * Creates storage backed by the supplied relative or absolute path.
     */
    public Storage(String filePath) {
        this(filePath, System.out::println);
    }

    /**
     * Creates storage with an error destination shared by the console or GUI.
     */
    public Storage(String filePath, Consumer<String> reportError) {
        this.file = Path.of(filePath);
        this.reportError = reportError;
    }

    /**
     * Saves all tasks, reporting I/O failures to the user.
     */
    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Files.write(file, tasks.stream().map(Task::toStorageString).toList());
        } catch (IOException | SecurityException e) {
            reportError.accept("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads valid task records and skips malformed records.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                try {
                    String[] fields = line.split("\\s*\\|\\s*", -1);
                    if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
                        continue;
                    }
                    Task task = switch (fields[0]) {
                        case "T" -> new Todo(fields[2]);
                        case "D" -> fields.length >= 4 ? new Deadline(fields[2], fields[3]) : null;
                        case "E" -> fields.length >= 5
                            ? new Event(fields[2], fields[3], fields[4]) : null;
                        default -> null;
                    };
                    if (task == null) {
                        continue;
                    }
                    if (fields[1].equals("1")) {
                        task.mark();
                    }
                    tasks.add(task);
                } catch (RuntimeException ignored) {
                    // Ignore malformed records and continue loading valid ones.
                }
            }
        } catch (IOException | SecurityException e) {
            reportError.accept("Unable to load tasks: " + e.getMessage());
        }
        return tasks;
    }
}
