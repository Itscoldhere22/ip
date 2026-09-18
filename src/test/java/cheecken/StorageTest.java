package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that storage restores valid records despite missing or malformed data.
 */
class StorageTest {
    @TempDir
    private Path directory;

    @Test
    void load_mixedRecords_preservesValidTasksInOrder() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, """
                T | 1 | read book
                invalid
                T | 2 | invalid status
                D | 0 | missing date
                D | 0 | invalid date | not-a-date
                D | 0 | return book | 2026-10-15
                E | 0 | missing end | 2026-10-15
                E | 0 | invalid end | 2026-10-15 | not-a-date
                X | 0 | unknown type
                E | 1 | meeting | 2026-10-15T09:00 | 2026-10-15T10:00
                T | 0 | last task
                """);
        List<String> errors = new ArrayList<>();
        Storage storage = new Storage(file.toString(), errors::add);

        List<String> tasks = storage.load().stream().map(Task::toString).toList();

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Oct 15 2026)",
                "[E][X] meeting (from: Oct 15 2026 9:00 AM to: Oct 15 2026 10:00 AM)",
                "[T][ ] last task"), tasks);
        assertTrue(errors.isEmpty());
    }

    @Test
    void load_missingFile_returnsEmptyListWithoutError() {
        List<String> errors = new ArrayList<>();
        Storage storage = new Storage(directory.resolve("missing.txt").toString(), errors::add);

        assertTrue(storage.load().isEmpty());
        assertTrue(errors.isEmpty());
    }
}
