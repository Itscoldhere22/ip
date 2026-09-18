package cheecken;

import java.util.Scanner;

/**
 * Runs the Cheecken chatbot and coordinates command handling, storage, and UI.
 */
public class Cheecken {
    private static final TaskList list = new TaskList();
    private static final Storage storage = new Storage("data/cheecken.txt");
    private static final Parser parser = new Parser();
    private static final Ui ui = new Ui();

    /**
     * Processes one raw command and returns whether the chatbot should exit.
     * @param rawInput command entered by the user
     * @return 1 when the command requests exit; otherwise 0
     */
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
                case FIND -> handleFind(input);
            };
        } catch (Exception e) {
            ui.showError(e);
            return 0;
        }
    }

    /**
     * Displays the farewell message.
     * @return 1 to terminate the command loop
     */
    private static int handleBye() {
        ui.showBye();
        return 1;
    }

    /**
     * Displays all tasks currently in the task list.
     * @return 0 to continue the command loop
     */
    private static int handleList() {
        ui.showList(list.asList());
        return 0;
    }

    /**
     * Marks the task selected by a one-based index as complete.
     * @param input complete mark command
     * @return 0 to continue the command loop
     */
    private static int handleMark(String input) {
        Task task = list.get(parseIndex(input, "mark"));
        task.mark();
        saveTasks();
        ui.showTaskMessage("Nice! I've marked this task as done:", task);
        return 0;
    }

    /**
     * Marks the task selected by a one-based index as incomplete.
     * @param input complete unmark command
     * @return 0 to continue the command loop
     */
    private static int handleUnmark(String input) {
        Task task = list.get(parseIndex(input, "unmark"));
        task.unmark();
        saveTasks();
        ui.showTaskMessage("OK, I've marked this task as not done yet:", task);
        return 0;
    }

    /**
     * Creates and stores a todo task from the command text.
     * @param input complete todo command
     * @return 0 to continue the command loop
     */
    private static int handleTodo(String input) {
        if (input.length() <= 4) {
            throw new CheeckenEmptyException("todo");
        }
        String taskText = input.substring(5);
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("todo");
        }
        addTask(storeMsg(taskText));
        return 0;
    }

    /**
     * Creates and stores a deadline task from the command text.
     * @param input complete deadline command
     * @return 0 to continue the command loop
     */
    private static int handleDeadline(String input) {
        int slash = input.indexOf("/");
        if (slash == -1) {
            if (input.substring(8).isBlank()) {
                throw new CheeckenEmptyException("deadline");
            }
            throw new CheeckenDateTimeException("deadline");
        }
        String taskText = input.substring(8, slash).strip();
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("deadline");
        }
        if (input.substring(slash + 3).isBlank()) {
            throw new CheeckenDateTimeException("deadline");
        }
        String deadline = input.substring(slash + 4);
        addTask(storeMsg(taskText, deadline));
        return 0;
    }

    /**
     * Creates and stores an event task from the command text.
     * @param input complete event command
     * @return 0 to continue the command loop
     */
    private static int handleEvent(String input) {
        int from = input.indexOf("/from");
        int to = input.indexOf("/to");
        if (from == -1) {
            if (input.substring(5).isBlank()) {
                throw new CheeckenEmptyException("event");
            }
            throw new CheeckenDateTimeException("event");
        }
        String taskText = input.substring(5, from).strip();
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("event");
        }
        if (to == -1) {
            throw new CheeckenDateTimeException("event");
        }
        if (input.substring(from + 5, to).isBlank() || input.substring(to + 3).isBlank()) {
            throw new CheeckenDateTimeException("event");
        }
        String start = input.substring(from + 5, to).strip();
        String end = input.substring(to + 3).strip();
        addTask(storeMsg(taskText, start, end));
        return 0;
    }

    /**
     * Deletes the task selected by a one-based index.
     * @param input complete delete command
     * @return 0 to continue the command loop
     */
    private static int handleDelete(String input) {
        Task task = deleteTask(parseIndex(input, "delete"));
        ui.showTaskMessage("Noted. I've removed this task:", task);
        return 0;
    }

    /**
     * Displays tasks matching the supplied nonempty search keyword.
     */
    private static int handleFind(String input) {
        String keyword = input.substring("find".length()).strip();
        if (keyword.isBlank()) {
            throw new CheeckenFindException();
        }
        ui.showFind(list.find(keyword));
        return 0;
    }

    /**
     * Displays confirmation after a task has been added.
     * @param task newly added task
     */
    private static void addTask(Task task) {
        ui.showAdded(task, list.size());
    }

    /**
     * Creates and persists a todo task.
     * @param task task description
     * @return created todo task
     */
    private static Todo storeMsg(String task) {
        Todo newTodo = new Todo(task);
        list.add(newTodo);
        saveTasks();

        return newTodo;
    }

    /**
     * Creates and persists a deadline task.
     * @param task task description
     * @param deadline deadline value
     * @return created deadline task
     */
    private static Deadline storeMsg(String task, String deadline) {
        Deadline newDeadline = new Deadline(task, deadline);
        list.add(newDeadline);
        saveTasks();

        return newDeadline;
    }

    /**
     * Creates and persists an event task.
     * @param task task description
     * @param startTime event start value
     * @param endTime event end value
     * @return created event task
     */
    private static Event storeMsg(String task, String startTime, String endTime) {
        Event newEvent = new Event(task, startTime, endTime);
        list.add(newEvent);
        saveTasks();

        return newEvent;
    }

    /**
     * Removes and persists the task at the specified zero-based index.
     * @param taskIndex zero-based task index
     * @return removed task
     */
    private static Task deleteTask(int taskIndex) {
        Task task = list.remove(taskIndex);
        saveTasks();
        return task;
    }

    /**
     * Converts the numeric argument of a command into a validated index.
     * @param input complete command
     * @param command command keyword
     * @return zero-based task index
     */
    private static int parseIndex(String input, String command) {
        String value = input.substring(command.length()).strip();
        int index = Integer.parseInt(value) - 1;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Task index out of range");
        }
        return index;
    }

    /**
     * Persists the current task list, reporting storage failures to the user.
     */
    private static void saveTasks() {
        try {
            storage.save(list.asList());
        } catch (RuntimeException e) {
            System.out.println("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads valid persisted tasks when the chatbot starts.
     */
    private static void loadTasks() {
        storage.load().forEach(list::add);
    }

    /**
     * Starts the interactive chatbot loop and processes commands until exit.
     */
    public void run() {
        loadTasks();
        ui.showWelcome();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();
            int echoRes = echo(input);
            if (echoRes == 1) {
                break;
            }
        }
        scanner.close();
    }

    /**
     * Launches the Cheecken chatbot application.
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        new Cheecken().run();
    }
}
