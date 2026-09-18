package cheecken;

import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Runs the Cheecken chatbot and coordinates command handling, storage, and UI.
 */
public class Cheecken {
    private final TaskList list = new TaskList();
    private final Storage storage;
    private final Parser parser = new Parser();
    private final Ui ui;
    private final StringBuilder response = new StringBuilder();
    private boolean isFinished;
    private boolean isLoaded;

    /**
     * Creates a chatbot using the normal persistence file.
     */
    public Cheecken() {
        this("data/cheecken.txt");
    }

    /**
     * Creates an independent chatbot with a chosen persistence file.
     */
    public Cheecken(String filePath) {
        ui = new Ui(this::appendResponse);
        storage = new Storage(filePath, this::appendResponse);
    }

    /**
     * Adds output to the current response without redirecting global console streams.
     */
    private void appendResponse(String message) {
        response.append(message).append("\n");
    }

    /**
     * Loads persisted tasks once and returns any startup warnings.
     */
    public String initialize() {
        response.setLength(0);
        if (!isLoaded) {
            loadTasks();
            isLoaded = true;
        }
        return response.toString().strip();
    }

    /**
     * Executes one command and returns plain text for a chat bubble.
     */
    public String getResponse(String input) {
        response.setLength(0);
        if (isFinished) {
            return "This conversation has ended. Close the window to exit.";
        }
        if (!isLoaded) {
            loadTasks();
            isLoaded = true;
        }
        isFinished = echo(input) == 1;
        return response.toString().lines()
                .filter(line -> !line.equals("____________________________________________________________"))
                .collect(Collectors.joining("\n")).strip();
    }

    /**
     * Returns whether the user has ended the conversation with bye.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Processes one raw command and returns whether the chatbot should exit.
     * @param rawInput command entered by the user
     * @return 1 when the command requests exit; otherwise 0
     */
    private int echo(String rawInput) {
        // Both entry points must load persisted tasks before accepting commands.
        assert isLoaded : "Tasks must be loaded before processing commands";
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
    private int handleBye() {
        ui.showBye();
        return 1;
    }

    /**
     * Displays all tasks currently in the task list.
     * @return 0 to continue the command loop
     */
    private int handleList() {
        ui.showList(list.asList());
        return 0;
    }

    /**
     * Marks the task selected by a one-based index as complete.
     * @param input complete mark command
     * @return 0 to continue the command loop
     */
    private int handleMark(String input) {
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
    private int handleUnmark(String input) {
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
    private int handleTodo(String input) {
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
    private int handleDeadline(String input) {
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
    private int handleEvent(String input) {
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
    private int handleDelete(String input) {
        Task task = deleteTask(parseIndex(input, "delete"));
        ui.showTaskMessage("Noted. I've removed this task:", task);
        return 0;
    }

    /**
     * Displays tasks matching the supplied nonempty search keyword.
     */
    private int handleFind(String input) {
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
    private void addTask(Task task) {
        // Each storeMsg overload must append the task before confirming it.
        assert list.size() > 0 && list.get(list.size() - 1) == task
                : "The added task must be the last task in the list";
        ui.showAdded(task, list.size());
    }

    /**
     * Creates and persists a todo task.
     * @param task task description
     * @return created todo task
     */
    private Todo storeMsg(String task) {
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
    private Deadline storeMsg(String task, String deadline) {
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
    private Event storeMsg(String task, String startTime, String endTime) {
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
    private Task deleteTask(int taskIndex) {
        // The command handler must validate user input through parseIndex first.
        assert taskIndex >= 0 && taskIndex < list.size() : "Deletion requires a validated task index";
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
    private int parseIndex(String input, String command) {
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
    private void saveTasks() {
        try {
            storage.save(list.asList());
        } catch (RuntimeException e) {
            appendResponse("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads valid persisted tasks when the chatbot starts.
     */
    private void loadTasks() {
        // Loading twice would append duplicate records to the in-memory list.
        assert !isLoaded && list.size() == 0 : "Tasks must only be loaded into an uninitialized, empty list";
        storage.load().forEach(list::add);
    }

    /**
     * Starts the interactive chatbot loop and processes commands until exit.
     */
    public void run() {
        initialize();
        ui.showWelcome();
        System.out.print(response);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();
            response.setLength(0);
            int echoRes = echo(input);
            System.out.print(response);
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
