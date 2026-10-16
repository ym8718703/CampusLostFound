package campuslostfound;

/**
 * Custom Exception: Thrown when item validation fails.
 * Demonstrates: Exception Handling (Unit II, CO3).
 */
public class InvalidItemException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidItemException(String message) {
        super(message);
    }
}
