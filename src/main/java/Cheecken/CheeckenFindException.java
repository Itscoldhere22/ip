package Cheecken;

/** Reports that a find command did not include a search keyword. */
public class CheeckenFindException extends RuntimeException {
    public CheeckenFindException() {
        super("Please provide a keyword to search for.");
    }
}
