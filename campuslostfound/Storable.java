package campuslostfound;

/**
 * Interface for objects that can be persisted as a line of text in files.
 * Demonstrates: Interfaces and Full Abstraction (Unit I, CO2).
 */
public interface Storable {
    /**
     * Converts the object data into a delimiter-separated text line.
     * @return Formatted line for text file storage.
     */
    String toFileString();
}
