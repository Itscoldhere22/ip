import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";
    private static final Task[] list = new Task[100];
    private static int listLength = 0;

    private static int echo(String input) {

        if (input.equals("bye")) {
            String byeMsg = """
                    ____________________________________________________________
                    Bye. Hope to see you again soon!
                    ____________________________________________________________
                    """;
            System.out.println(byeMsg);

            return 1;
        } else if (input.equals("list")) {
            System.out.println("____________________________________________________________");
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < listLength; i++)
                System.out.println((i + 1) + "." + list[i]);
            System.out.println("____________________________________________________________");

            return 0;
        } else if (input.startsWith("mark")) {
            String inputIndex = input.substring(5);
            int listIndex = Integer.parseInt(inputIndex) - 1;

            Task task = list[listIndex];
            task.mark();
            String msg = "Nice! I've marked this task as done:\n  " + task;

            System.out.println("____________________________________________________________");
            System.out.println(msg);
            System.out.println("____________________________________________________________");
            return 0;
        } else if (input.startsWith("unmark")) {
            String inputIndex = input.substring(7);
            int listIndex = Integer.parseInt(inputIndex) - 1;

            Task task = list[listIndex];
            task.unmark();
            String msg = "OK, I've marked this task as not done yet:\n  " + task;

            System.out.println("____________________________________________________________");
            System.out.println(msg);
            System.out.println("____________________________________________________________");
            return 0;
        } else if (input.startsWith("deadline")) {
            int forwardSlashIndex = input.indexOf("/");
            String inputTask = input.substring(9, forwardSlashIndex - 1);
            String deadline = input.substring(forwardSlashIndex + 4);

            Task newDeadline = storeMsg(inputTask, deadline);

            String msg = "____________________________________________________________\n" +
                        "Got it. I've added this task:\n" +
                        "  " + newDeadline +
                        "\nNow you have " + listLength + " tasks in the list.\n" +
                        "____________________________________________________________\n";
            System.out.println(msg);

            return 0;
        } else if (input.startsWith("event")) {
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");
            String inputTask = input.substring(6, fromIndex - 1);
            String startTime = input.substring(fromIndex + 6, toIndex - 1);
            String endTime = input.substring(toIndex + 4);

            Task newEvent = storeMsg(inputTask, startTime, endTime);
            String msg = "____________________________________________________________\n" +
                        "Got it. I've added this task:\n" +
                        "  " + newEvent +
                        "\nNow you have " + listLength + " tasks in the list.\n" +
                        "____________________________________________________________\n";
            System.out.println(msg);

            return 0;
        } else if (input.startsWith("todo")) {
            String inputTask = input.substring(5);
            Task newTodo = storeMsg(inputTask);
            String msg = "____________________________________________________________\n" +
                    "Got it. I've added this task:\n" +
                    "  " + newTodo +
                    "\nNow you have " + listLength + " tasks in the list.\n" +
                    "____________________________________________________________\n";
            System.out.println(msg);

            return 0;
        }
        return 0;
    }

    private static Todo storeMsg(String task) {
        Todo newTodo = new Todo(task);
        list[listLength] = newTodo;
        listLength++;

        return newTodo;
    }

    private static Deadline storeMsg(String task, String deadline) {
        Deadline newDeadline = new Deadline(task, deadline);
        list[listLength] = newDeadline;
        listLength++;

        return newDeadline;
    }

    private static Event storeMsg(String task, String startTime, String endTime) {
        Event newEvent = new Event(task, startTime, endTime);
        list[listLength] = newEvent;
        listLength++;

        return newEvent;
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
