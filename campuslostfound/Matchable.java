package campuslostfound;

/**
 * Interface for comparison between lost and found items.
 * Demonstrates: Interfaces, Interface Constants, and Abstraction (Unit I, CO2).
 */
public interface Matchable {
    // Interface constant (implicitly public static final)
    int MATCH_THRESHOLD = 50;

    /**
     * Calculates match confidence score (0 to 100) between this item and another item.
     * @param other The item to compare against.
     * @return Match score between 0 and 100.
     */
    int calculateMatchScore(Item other);
}
