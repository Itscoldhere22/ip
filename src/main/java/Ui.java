import java.util.List;

/** Handles all user-facing console output. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    public void showWelcome() {
        System.out.println(" _____ _                    _              \n"
                + "/  __ \\ |                  | |             \n"
                + "| /  \\/ |__   ___  ___  ___| | _____ _ __  \n"
                + "| |   | '_ \\ / _ \\/ _ \\/ __| |/ / _ \\ '_ \\ \n"
                + "| \\__/\\ | | |  __/  __/ (__|   <  __/ | | |\n"
                + " \\____/_| |_|\\___|\\___|\\___|_|\\_\\___|_| |_|\n"
                + SEPARATOR + "\nHello! I'm \033[3mCHEECKEN\033[0m.\n"
                + "What can I do for you?\n" + SEPARATOR);
    }

    public void showBye() {
        System.out.println(SEPARATOR + "\nBye. Hope to see you again soon!\n" + SEPARATOR);
    }

    public void showList(List<Task> tasks) {
        System.out.println(SEPARATOR + "\nHere are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(SEPARATOR);
    }

    public void showAdded(Task task, int count) {
        System.out.println(SEPARATOR + "\nGot it. I've added this task:\n  " + task
                + "\nNow you have " + count + " tasks in the list.\n" + SEPARATOR);
    }

    public void showTaskMessage(String message, Task task) {
        System.out.println(SEPARATOR + "\n" + message + "\n  " + task + "\n" + SEPARATOR);
    }

    public void showError(Exception exception) {
        System.out.println(SEPARATOR);
        System.out.println(exception.getMessage());
    }
}
