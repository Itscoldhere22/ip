public class CheeckenUnknownException extends RuntimeException {
    public CheeckenUnknownException() {
        super("""
                What do you want? I only understand sentences starting with `todo`, `event` and `deadline.`
                (e.g. `event her wedding /from later /to forever`)"""
        );
    }
}
