public class CheeckenEmptyException extends RuntimeException {
    public CheeckenEmptyException(String task) {
        super("There's no task that is empty. LOCK INNN!\n" + switch (task) {
        case "todo" -> "(e.g. todo buy her flowers)";
        case "event" -> "(e.g. event love me /from 15/10/2025 0900 /to 15/10/3000 1100)";
        case "deadline" -> "(e.g. deadline buy her flowers /by 15/10/2025 1800)";
        default -> "(e.g. todo buy her flowers)";
        });
    }

    public CheeckenEmptyException(boolean by) {
        super("What's a deadlined task without the deadline??\n(e.g. deadline have a baby /by tonight");
    }

    public CheeckenEmptyException(boolean start, boolean end) {
        super("When your event starts la\n(e.g. event some random event /from 2pm /to 2pm ish?)");
    }
}
