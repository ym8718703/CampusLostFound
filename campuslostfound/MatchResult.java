package campuslostfound;

/**
 * Encapsulates a potential match between a LostItem and a FoundItem.
 *
 * Demonstrates Syllabus Concepts:
 * - Comparable Interface: Implements java.lang.Comparable to allow sorting with Collections.sort()
 *   in descending order of match confidence (Unit II, CO4).
 * - Encapsulation: Private member variables with public getters (Unit I, CO2).
 */
public class MatchResult implements Comparable<MatchResult> {
    private final LostItem lostItem;
    private final FoundItem foundItem;
    private final int score;

    public MatchResult(LostItem lostItem, FoundItem foundItem, int score) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.score = score;
    }

    public LostItem getLostItem()   { return lostItem; }
    public FoundItem getFoundItem() { return foundItem; }
    public int getScore()           { return score; }

    /**
     * Compare by score descending (highest score first).
     */
    @Override
    public int compareTo(MatchResult other) {
        return other.score - this.score;
    }

    @Override
    public String toString() {
        return "Match [" + score + "%] -> Lost: " + lostItem.getName() + " (" + lostItem.getId() + ") <-> Found: "
                + foundItem.getName() + " (" + foundItem.getId() + ")";
    }
}
