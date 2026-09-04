/** Indicates that a deadline or event date/time is missing or invalid. */
public class CheeckenDateTimeException extends RuntimeException {
    public CheeckenDateTimeException(String command) {
        super("No time how I set the task...\n" + (command.equals("event")
                ? "(e.g. event buying her flowers /from 15/10/2025 0900 /to 15/10/2025 1100)"
                : "(e.g. deadline buy her flowers /by 15/10/2025 1800)"));
    }
}
