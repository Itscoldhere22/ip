/**
 * Commands understood by the Cheecken command-line interface.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DEADLINE("deadline"),
    EVENT("event"),
    TODO("todo"),
    DELETE("delete");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns whether the input starts with this command keyword.
     * Commands without arguments require an exact match.
     */
    public boolean matches(String input) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }
}
