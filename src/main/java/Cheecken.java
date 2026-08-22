public class Cheecken {
    public static final String ITALIC = "\033[3m";
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        String banner = "   ___ _                    _              \n" +
                "  / __\\ |__   ___  ___  ___| | _____ _ __  \n" +
                " / /  | '_ \\ / _ \\/ _ \\/ __| |/ / _ \\ '_ \\\n" +
                "/ /___| | | |  __/  __/ (__|   <  __/ | | |\n" +
                "\\____/|_| |_|\\___|\\___|\\___|_|\\_\\___|_| |_|";
        String message = """
                ____________________________________________________________
                Hello! I'm \033[3mCHEECKEN\033[0m.
                What can I do for you?
                ____________________________________________________________
                Bye. Hope to see you again soon!
                ____________________________________________________________""";
        System.out.println(banner);
        System.out.println(message);
    }
}
