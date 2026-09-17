import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";
    private static final TaskList list = new TaskList();
    private static final Storage storage = new Storage("data/cheecken.txt");
    private static final Parser parser = new Parser();

    private static int echo(String rawInput) {
        String input = parser.normalize(rawInput);
        CommandType command = parser.parseCommand(input);
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
            System.out.println("____________________________________________________________");
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
        Task task = list.get(parseIndex(input, "mark"));
        task.mark();
        saveTasks();
        printTaskMessage("Nice! I've marked this task as done:", task);
        return 0;
    }

    private static int handleUnmark(String input) {
        Task task = list.get(parseIndex(input, "unmark"));
        task.unmark();
        saveTasks();
        printTaskMessage("OK, I've marked this task as not done yet:", task);
        return 0;
    }

    private static int handleTodo(String input) {
        if (input.length() <= 4) throw new CheeckenEmptyException("todo");
        String taskText = input.substring(5);
        if (taskText.isBlank()) throw new CheeckenEmptyException("todo");
        addTask(storeMsg(taskText));
        return 0;
    }

    private static int handleDeadline(String input) {
        int slash = input.indexOf("/");
        if (slash == -1) {
            if (input.substring(8).isBlank()) throw new CheeckenEmptyException("deadline");
            throw new CheeckenDateTimeException("deadline");
        }
        String taskText = input.substring(8, slash).strip();
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("deadline");
        }
        if (input.substring(slash + 3).isBlank()) throw new CheeckenDateTimeException("deadline");
        String deadline = input.substring(slash + 4);
        addTask(storeMsg(taskText, deadline));
        return 0;
    }

    private static int handleEvent(String input) {
        int from = input.indexOf("/from");
        int to = input.indexOf("/to");
        if (from == -1) {
            if (input.substring(5).isBlank()) throw new CheeckenEmptyException("event");
            throw new CheeckenDateTimeException("event");
        }
        String taskText = input.substring(5, from).strip();
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("event");
        }
        if (to == -1) throw new CheeckenDateTimeException("event");
        if (input.substring(from + 5, to).isBlank() || input.substring(to + 3).isBlank()) {
            throw new CheeckenDateTimeException("event");
        }
        String start = input.substring(from + 5, to).strip();
        String end = input.substring(to + 3).strip();
        addTask(storeMsg(taskText, start, end));
        return 0;
    }

    private static int handleDelete(String input) {
        Task task = deleteTask(parseIndex(input, "delete"));
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
        saveTasks();

        return newTodo;
    }

    private static Deadline storeMsg(String task, String deadline) {
        Deadline newDeadline = new Deadline(task, deadline);
        list.add(newDeadline);
        saveTasks();

        return newDeadline;
    }

    private static Event storeMsg(String task, String startTime, String endTime) {
        Event newEvent = new Event(task, startTime, endTime);
        list.add(newEvent);
        saveTasks();

        return newEvent;
    }

    private static Task deleteTask(int taskIndex) {
        Task task = list.remove(taskIndex);
        saveTasks();
        return task;
    }

    private static int parseIndex(String input, String command) {
        String value = input.substring(command.length()).strip();
        int index = Integer.parseInt(value) - 1;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Task index out of range");
        }
        return index;
    }

    private static void saveTasks() {
        try {
            storage.save(list.asList());
        } catch (RuntimeException e) {
            System.out.println("Unable to save tasks: " + e.getMessage());
        }
    }

    /** Loads valid persisted tasks when the chatbot starts. */
    private static void loadTasks() {
        storage.load().forEach(list::add);
    }

    public static void main(String[] args) {
        loadTasks();
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
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();
            int echoRes = echo(input);
            if (echoRes == 1)
                break ;
        }
        scanner.close();
    }
}
