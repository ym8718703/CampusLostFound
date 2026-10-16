package campuslostfound;

/**
 * Custom Exception: Thrown when an item with a given ID is not found.
 * Demonstrates: Exception Handling (Unit II, CO3).
 */
public class ItemNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String id) {
        super("Item with ID '" + id + "' was not found in the records.");
    }
}
