import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CheeckenUiTest {
    static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of("Exit with bye", "bye\n", "Bye. Hope to see you again soon!"),
            Arguments.of("Reject empty todo", "todo\n", "There's no task that is empty."),
            Arguments.of("Add a todo task", "todo buy her flowers\n", "[T][ ] buy her flowers"),
            Arguments.of("Reject empty deadline", "deadline\n", "There's no task that is empty."),
            Arguments.of("Reject deadline without task", "deadline /by\n", "There's no task that is empty."),
            Arguments.of("Reject deadline without date", "deadline buy her flowers\n", "No time how I set the task..."),
            Arguments.of("Reject deadline with empty date", "deadline buy her flowers /by\n", "No time how I set the task..."),
            Arguments.of("Add deadline with 12-hour time", "deadline buy her flowers /by 15/10/2025 1800\n", "Oct 15 2025 6:00 PM"),
            Arguments.of("Reject empty event", "event\n", "There's no task that is empty."),
            Arguments.of("Reject event without task", "event /from /to\n", "There's no task that is empty."),
            Arguments.of("Reject event without datetimes", "event love me\n", "No time how I set the task..."),
            Arguments.of("Add event with 12-hour times", "event love me /from 15/10/2025 0900 /to 15/10/3000 1100\n", "9:00 AM"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void commandProducesExpectedOutput(String name, String input, String expected) throws Exception {
        String output = run(input, null);
        assertTrue(output.contains(expected), () -> "Expected: " + expected + "\nActual:\n" + output);
    }

    @org.junit.jupiter.api.Test
    void loadsPersistedTask() throws Exception {
        String output = run("list\nbye\n", "T | 1 | read book\n");
        assertTrue(output.contains("1.[T][X] read book"));
    }

    @org.junit.jupiter.api.Test
    void persistsTaskChanges() throws Exception {
        Path dir = Files.createTempDirectory("cheecken-ui-");
        Files.createDirectories(dir.resolve("data"));
        ProcessBuilder pb = process("todo read book\nbye\n", dir);
        Process process = pb.start();
        process.getOutputStream().write("todo read book\nbye\n".getBytes());
        process.getOutputStream().close();
        process.waitFor();
        assertTrue(Files.readString(dir.resolve("data/cheecken.txt")).contains("T | 0 | read book"));
    }

    private static String run(String input, String persisted) throws Exception {
        Path dir = Files.createTempDirectory("cheecken-ui-");
        if (persisted != null) {
            Files.createDirectories(dir.resolve("data"));
            Files.writeString(dir.resolve("data/cheecken.txt"), persisted);
        }
        Process process = process(input, dir).start();
        process.getOutputStream().write(input.getBytes());
        process.getOutputStream().close();
        return new String(process.getInputStream().readAllBytes());
    }

    private static ProcessBuilder process(String input, Path dir) {
        return new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), "Cheecken")
                .directory(dir.toFile()).redirectErrorStream(true).redirectInput(ProcessBuilder.Redirect.PIPE);
    }
}
