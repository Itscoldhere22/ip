import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";
    private static final List<Task> list = new ArrayList<>();

    private static int echo(String rawInput) {
        String input = rawInput.strip();
        CommandType command = CommandType.fromInput(input);
        try {
            if (command == null) {
                throw new CheeckenUnknownException();
            }
            return switch (command) {
            case BYE -> handleBye();
            case LIST -> handleList();
            case MARK -> handleMark(input);
            case UNMARK -> handleUnmark(input);
            case DEADLINE -> handleDeadline(input);
            case EVENT -> handleEvent(input);
            case TODO -> handleTodo(input);
            case DELETE -> handleDelete(input);
            };
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private static int handleBye() {
        System.out.println("____________________________________________________________\n"
                + "Bye. Hope to see you again soon!\n"
                + "____________________________________________________________");
        return 1;
    }

    private static int handleList() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + "." + list.get(i));
        }
        System.out.println("____________________________________________________________");
        return 0;
    }

    private static int handleMark(String input) {
        Task task = list.get(Integer.parseInt(input.substring(5)) - 1);
        task.mark();
        printTaskMessage("Nice! I've marked this task as done:", task);
        return 0;
    }

    private static int handleUnmark(String input) {
        Task task = list.get(Integer.parseInt(input.substring(7)) - 1);
        task.unmark();
        printTaskMessage("OK, I've marked this task as not done yet:", task);
        return 0;
    }

    private static int handleTodo(String input) {
        String taskText = input.substring(5);
        if (taskText.isEmpty()) throw new CheeckenEmptyException("todo");
        addTask(storeMsg(taskText));
        return 0;
    }

    private static int handleDeadline(String input) {
        int slash = input.indexOf("/");
        if (slash == -1) throw new CheeckenEmptyException(false);
        String taskText = input.substring(9, slash - 1);
        if (taskText.isEmpty()) throw new CheeckenEmptyException("deadline");
        String deadline = input.substring(slash + 4);
        addTask(storeMsg(taskText, deadline));
        return 0;
    }

    private static int handleEvent(String input) {
        int from = input.indexOf("/from");
        int to = input.indexOf("/to");
        if (from == -1) throw new CheeckenEmptyException(false, false);
        if (to == -1) throw new CheeckenEmptyException(true, false);
        String taskText = input.substring(6, from - 1);
        if (taskText.isEmpty()) throw new CheeckenEmptyException("event");
        String start = input.substring(from + 6, to - 1);
        String end = input.substring(to + 4);
        addTask(storeMsg(taskText, start, end));
        return 0;
    }

    private static int handleDelete(String input) {
        Task task = deleteTask(Integer.parseInt(input.substring(7)) - 1);
        printTaskMessage("Noted. I've removed this task:", task);
        return 0;
    }

    private static void addTask(Task task) {
        System.out.println("____________________________________________________________\n"
                + "Got it. I've added this task:\n  " + task
                + "\nNow you have " + list.size() + " tasks in the list.\n"
                + "____________________________________________________________");
    }

    private static void printTaskMessage(String message, Task task) {
        System.out.println("____________________________________________________________");
        System.out.println(message + "\n  " + task);
        System.out.println("____________________________________________________________");
    }

    private static Todo storeMsg(String task) {
        Todo newTodo = new Todo(task);
        list.add(newTodo);

        return newTodo;
    }

    private static Deadline storeMsg(String task, String deadline) {
        Deadline newDeadline = new Deadline(task, deadline);
        list.add(newDeadline);

        return newDeadline;
    }

    private static Event storeMsg(String task, String startTime, String endTime) {
        Event newEvent = new Event(task, startTime, endTime);
        list.add(newEvent);

        return newEvent;
    }

    private static Task deleteTask(int taskIndex) {
        return list.remove(taskIndex);
    }

    public static void main(String[] args) {
        String welcomeMsg = """
                 _____ _                    _             \s
                /  __ \\ |                  | |            \s
                | /  \\/ |__   ___  ___  ___| | _____ _ __ \s
                | |   | '_ \\ / _ \\/ _ \\/ __| |/ / _ \\ '_ \\\s
                | \\__/\\ | | |  __/  __/ (__|   <  __/ | | |
                 \\____/_| |_|\\___|\\___|\\___|_|\\_\\___|_| |_|
                ____________________________________________________________
                Hello! I'm \033[3mCHEECKEN\033[0m.
                What can I do for you?
                ____________________________________________________________""";

        System.out.println(welcomeMsg);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            int echoRes = echo(input);
            if (echoRes == 1)
                break ;
        }
        scanner.close();
    }
}
