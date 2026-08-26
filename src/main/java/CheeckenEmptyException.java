public class CheeckenEmptyException extends RuntimeException {
    public CheeckenEmptyException(String task) {
        super("There's no task that is empty. LOCK INNN!\n" +
                "(e.g. todo buy her flowers\n" +
                "      event buying her flowers /from today /to every day\n" +
                "      deadline buy her flowers /by before she leaves you)");
    }

    public CheeckenEmptyException(boolean by) {
        super("What's a deadlined task without the deadline??\n(e.g. deadline have a baby /by tonight");
    }

    public CheeckenEmptyException(boolean start, boolean end) {
        super("When your event starts la\n(e.g. event some random event /from 2pm /to 2pm ish?)");
    }
}
