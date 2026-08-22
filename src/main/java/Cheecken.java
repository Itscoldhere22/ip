import java.util.Scanner;

public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";

    private static void echo(String input) {
        String msg = "____________________________________________________________\n" +
                input + "\n" +
                "____________________________________________________________\n";

        System.out.println(msg);
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
        String byeMsg = """
                Bye. Hope to see you again soon!
                ____________________________________________________________
                """;

        System.out.println(welcomeMsg);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(byeMsg);
                break ;
            }
            echo(input);
        }
        scanner.close();
    }
}
