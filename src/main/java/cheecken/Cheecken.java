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
        isFinished = executeCommand(input);
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
     * @return true when the command requests exit; otherwise false
     */
    private boolean executeCommand(String rawInput) {
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
            return false;
        }
    }

    /**
     * Displays the farewell message.
     * @return true to terminate the command loop
     */
    private boolean handleBye() {
        ui.showBye();
        return true;
    }

    /**
     * Displays all tasks currently in the task list.
     * @return false to continue the command loop
     */
    private boolean handleList() {
        ui.showList(list.asList());
        return false;
    }

    /**
     * Marks the task selected by a one-based index as complete.
     * @param input complete mark command
     * @return false to continue the command loop
     */
    private boolean handleMark(String input) {
        Task task = list.get(parseIndex(input, "mark"));
        task.mark();
        saveTasks();
        ui.showTaskMessage("Nice! I've marked this task as done:", task);
        return false;
    }

    /**
     * Marks the task selected by a one-based index as incomplete.
     * @param input complete unmark command
     * @return false to continue the command loop
     */
    private boolean handleUnmark(String input) {
        Task task = list.get(parseIndex(input, "unmark"));
        task.unmark();
        saveTasks();
        ui.showTaskMessage("OK, I've marked this task as not done yet:", task);
        return false;
    }

    /**
     * Creates and stores a todo task from the command text.
     * @param input complete todo command
     * @return false to continue the command loop
     */
    private boolean handleTodo(String input) {
        if (input.length() <= 4) {
            throw new CheeckenEmptyException("todo");
        }
        String taskText = input.substring(5);
        if (taskText.isBlank()) {
            throw new CheeckenEmptyException("todo");
        }
        addTask(new Todo(taskText));
        return false;
    }

    /**
     * Creates and stores a deadline task from the command text.
     * @param input complete deadline command
     * @return false to continue the command loop
     */
    private boolean handleDeadline(String input) {
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
        addTask(new Deadline(taskText, deadline));
        return false;
    }

    /**
     * Creates and stores an event task from the command text.
     * @param input complete event command
     * @return false to continue the command loop
     */
    private boolean handleEvent(String input) {
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
        addTask(new Event(taskText, start, end));
        return false;
    }

    /**
     * Deletes the task selected by a one-based index.
     * @param input complete delete command
     * @return false to continue the command loop
     */
    private boolean handleDelete(String input) {
        Task task = deleteTask(parseIndex(input, "delete"));
        ui.showTaskMessage("Noted. I've removed this task:", task);
        return false;
    }

    /**
     * Displays tasks matching the supplied nonempty search keyword.
     */
    private boolean handleFind(String input) {
        String keyword = input.substring("find".length()).strip();
        if (keyword.isBlank()) {
            throw new CheeckenFindException();
        }
        ui.showFind(list.find(keyword));
        return false;
    }

    /**
     * Adds and persists a task before displaying confirmation.
     * @param task task to add
     */
    private void addTask(Task task) {
        list.add(task);
        saveTasks();
        // Confirmation must describe the task just appended to the list.
        assert list.size() > 0 && list.get(list.size() - 1) == task
                : "The added task must be the last task in the list";
        ui.showAdded(task, list.size());
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
            boolean shouldExit = executeCommand(input);
            System.out.print(response);
            if (shouldExit) {
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
