package Cheecken;

/** Converts raw user input into a recognized command. */
public class Parser {
    /** Normalizes null-safe raw console input. */
    public String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.strip();
    }

    public CommandType parseCommand(String input) {
        String keyword = input.split("\\s+", 2)[0];
        for (CommandType command : CommandType.values()) {
            if (command.matches(input) || command.name().equalsIgnoreCase(keyword)) {
                return command;
            }
        }
        return null;
    }
}
    /** Identifies the command represented by normalized input. */
