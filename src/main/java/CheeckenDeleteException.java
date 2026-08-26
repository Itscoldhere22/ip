public class CheeckenDeleteException extends RuntimeException {
    public CheeckenDeleteException() {
        super("What are you trying to delete?\n(e.g. delete 3)");
    }
}
