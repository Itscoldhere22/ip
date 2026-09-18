package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks exact console formatting using an injected output destination.
 */
class UiTest {
    private static final String SEPARATOR = "____________________________________________________________";
    private final List<String> output = new ArrayList<>();
    private final Ui ui = new Ui(output::add);

    @Test
    void showList_empty_printsHeadingAndSeparators() {
        ui.showList(List.of());
        assertEquals(List.of(SEPARATOR + "\nHere are the tasks in your list:", SEPARATOR), output);
    }

    @Test
    void showList_multipleTasks_numbersTasksInOrder() {
        Task done = new Todo("done");
        done.mark();
        ui.showList(List.of(done, new Todo("pending")));
        assertEquals(List.of(SEPARATOR + "\nHere are the tasks in your list:",
                "1.[T][X] done", "2.[T][ ] pending", SEPARATOR), output);
    }

    @Test
    void showFind_noMatches_printsNoMatchMessage() {
        ui.showFind(List.of());
        assertEquals(List.of(SEPARATOR, "There are no matching tasks in your list.", SEPARATOR), output);
    }

    @Test
    void showFind_multipleMatches_numbersResultsFromOne() {
        ui.showFind(List.of(new Todo("book one"), new Todo("book two")));
        assertEquals(List.of(SEPARATOR, "Here are the matching tasks in your list:",
                "1.[T][ ] book one", "2.[T][ ] book two", SEPARATOR), output);
    }

    @Test
    void showAdded_taskAndCount_formatsConfirmation() {
        ui.showAdded(new Todo("read"), 2);
        assertEquals(List.of(SEPARATOR + "\nGot it. I've added this task:\n  [T][ ] read"
                + "\nNow you have 2 tasks in the list.\n" + SEPARATOR), output);
    }

    @Test
    void showTaskMessage_markedTask_preservesMessageAndTaskState() {
        Task task = new Todo("read");
        task.mark();
        ui.showTaskMessage("Updated:", task);
        assertEquals(List.of(SEPARATOR + "\nUpdated:\n  [T][X] read\n" + SEPARATOR), output);
    }

    @Test
    void showError_multilineMessage_preservesGuidance() {
        ui.showError(new IllegalArgumentException("Invalid input\nTry again"));
        assertEquals(List.of(SEPARATOR, "Invalid input\nTry again"), output);
    }

    @Test
    void showWelcomeAndBye_printsGreetingAndFarewell() {
        ui.showWelcome();
        assertEquals(1, output.size());
        assertTrue(output.get(0).endsWith(SEPARATOR + "\nHello! I'm \033[3mCHEECKEN\033[0m.\n"
                + "What can I do for you?\n" + SEPARATOR));
        output.clear();
        ui.showBye();
        assertEquals(List.of(SEPARATOR + "\nBye. Hope to see you again soon!\n" + SEPARATOR), output);
    }
}
