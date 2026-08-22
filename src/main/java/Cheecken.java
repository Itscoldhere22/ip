import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";
    private static final Task[] list = new Task[100];
    private static int listLength = 0;

    private static int echo(String input) {
        String msg = "____________________________________________________________\n" +
                "added: " + input + "\n" +
                "____________________________________________________________\n";
        String byeMsg = """
                Bye. Hope to see you again soon!
                ____________________________________________________________
                """;

        if (input.equals("bye")) {
            System.out.println(byeMsg);
            return 1;
        } else if (input.equals("list")) {
            System.out.println("____________________________________________________________");
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < listLength; i++)
                System.out.println((i + 1) + "." + list[i]);
            System.out.println("____________________________________________________________");
            return 2;
        } else if (input.startsWith("mark")) {
            String inputIndex = input.substring(5);
            int listIndex = Integer.parseInt(inputIndex) - 1;

            Task task = list[listIndex];
            task.mark();
            String markedMsg = "Nice! I've marked this task as done:\n  " + task;

            System.out.println("____________________________________________________________");
            System.out.println(markedMsg);
            System.out.println("____________________________________________________________");
            return 2;
        } else if (input.startsWith("unmark")) {
            String inputIndex = input.substring(7);
            int listIndex = Integer.parseInt(inputIndex) - 1;

            Task task = list[listIndex];
            task.unmark();
            String unmarkedMsg = "OK, I've marked this task as not done yet:\n  " + task;

            System.out.println("____________________________________________________________");
            System.out.println(unmarkedMsg);
            System.out.println("____________________________________________________________");
            return 2;
        }
        System.out.println(msg);
        return 0;
    }

    private static void storeMsg(String task) {
        list[listLength] = new Task(task);
        listLength++;
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
            else if (echoRes != 2)
                storeMsg(input);
        }
        scanner.close();
    }
}
