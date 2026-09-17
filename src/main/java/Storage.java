import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Handles loading and saving tasks in the persistence file. */
public class Storage {
    private final Path file;

    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, tasks.stream().map(Task::toStorageString).toList());
        } catch (IOException | SecurityException e) {
            System.out.println("Unable to save tasks: " + e.getMessage());
        }
    }

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
            System.out.println("Unable to load tasks: " + e.getMessage());
        }
        return tasks;
    }
}
