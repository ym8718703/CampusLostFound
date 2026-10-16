package campuslostfound;

import java.util.HashSet;
import java.util.Set;

/**
 * Subclass representing an item reported as lost by a student/staff member.
 *
 * Demonstrates Syllabus Concepts:
 * - Inheritance: Extends Item class using 'extends' and invokes super(...) constructor (Unit I, CO2)
 * - Interfaces: Implements Matchable interface (Unit I, CO2)
 * - Polymorphism: Overrides getType(), getPersonRole(), displayDetails(), and calculateMatchScore() (Unit I, CO2)
 * - Collections Framework: Uses HashSet to compute keyword intersection (Unit II, CO4)
 */
public class LostItem extends Item implements Matchable {

    /**
     * Parameterized Constructor
     */
    public LostItem(String id, String name, String category, String description,
                    String location, String date, String ownerName,
                    String ownerContact, String status) throws InvalidItemException {
        super(id, name, category, description, location, date, ownerName, ownerContact, status);
    }

    @Override
    public String getType() {
        return "LOST";
    }

    @Override
    public String getPersonRole() {
        return "Owner";
    }

    @Override
    public void displayDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("LOST ITEM RECORD: " + getId());
        System.out.println("   Item Name    : " + getName());
        System.out.println("   Category     : " + getCategory());
        System.out.println("   Description  : " + getDescription());
        System.out.println("   Place Lost   : " + getLocation());
        System.out.println("   Date Lost    : " + getDate());
        System.out.println("   Owner Name   : " + getPersonName());
        System.out.println("   Contact      : " + getPersonContact());
        System.out.println("   Status       : " + getStatus());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Scores how well a found item matches this lost item (0 to 100 points).
     *
     * Scoring Criteria:
     * 1. Category match: 40 points
     * 2. Keyword match (from name/description): up to 30 points
     * 3. Location match: up to 30 points
     */
    @Override
    public int calculateMatchScore(Item other) {
        if (!(other instanceof FoundItem)) {
            return 0;
        }

        int score = 0;

        // 1. Category Match (40 points)
        if (getCategory().equalsIgnoreCase(other.getCategory())) {
            score += 40;
        }

        // 2. Keyword Match (up to 30 points) using HashSet
        Set<String> myKeywords = getKeywords();
        Set<String> otherKeywords = other.getKeywords();

        int commonCount = 0;
        for (String word : myKeywords) {
            if (otherKeywords.contains(word)) {
                commonCount++;
            }
        }

        if (commonCount > 0) {
            if (commonCount >= 2) {
                score += 30;
            } else {
                score += 20;
            }
        }

        // 3. Location Match (up to 30 points)
        String myLoc = getLocation().toLowerCase();
        String otherLoc = other.getLocation().toLowerCase();
        if (myLoc.equals(otherLoc)) {
            score += 30;
        } else if (myLoc.contains(otherLoc) || otherLoc.contains(myLoc)) {
            score += 20;
        }

        // Safety rule: if there are zero keywords in common and categories differ,
        // do not mark as a match.
        if (commonCount == 0 && !getCategory().equalsIgnoreCase(other.getCategory())) {
            return 0;
        }

        if (score > 100) {
            score = 100;
        }

        return score;
    }
}
