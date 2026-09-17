import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";
    private static final TaskList list = new TaskList();
    private static final Storage storage = new Storage("data/cheecken.txt");
    private static final Parser parser = new Parser();
    private static final Ui ui = new Ui();

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
            ui.showError(e);
            return 0;
        }
    }

    private static int handleBye() {
        ui.showBye();
        return 1;
    }

    private static int handleList() {
        ui.showList(list.asList());
        return 0;
    }

    private static int handleMark(String input) {
        Task task = list.get(parseIndex(input, "mark"));
        task.mark();
        saveTasks();
        ui.showTaskMessage("Nice! I've marked this task as done:", task);
        return 0;
    }

    private static int handleUnmark(String input) {
        Task task = list.get(parseIndex(input, "unmark"));
        task.unmark();
        saveTasks();
        ui.showTaskMessage("OK, I've marked this task as not done yet:", task);
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
        ui.showAdded(task, list.size());
    }

    private static void printTaskMessage(String message, Task task) {
        ui.showTaskMessage(message, task);
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
        ui.showWelcome();
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
