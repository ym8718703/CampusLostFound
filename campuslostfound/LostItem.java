package campuslostfound;

/**
 * Subclass representing an item reported as lost by a student or staff member.
 *
 * Demonstrates Syllabus Concepts:
 * - Inheritance: Extends Item class using 'extends' and invokes super(...) constructor (Unit I, CO2)
 * - Interfaces: Implements Matchable interface (Unit I, CO2)
 * - Polymorphism: Overrides getType(), getPersonRole(), displayDetails(), and calculateMatchScore() (Unit I, CO2)
 * - String Processing: Simple string methods equalsIgnoreCase(), toLowerCase(), split(), contains() (Unit II, CO3)
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
     * Matches this lost item against a found item using simple Java concepts:
     * 1. Category comparison: String.equalsIgnoreCase() (40 points)
     * 2. Name / Description comparison: String.toLowerCase(), split(" "), and contains() (30 points)
     * 3. Location comparison: String.equalsIgnoreCase() or contains() (up to 30 points)
     *
     * Total score is out of 100 points.
     */
    @Override
    public int calculateMatchScore(Item other) {
        if (!(other instanceof FoundItem)) {
            return 0;
        }

        int score = 0;

        // 1. Category Match (40 points)
        boolean sameCategory = getCategory().equalsIgnoreCase(other.getCategory());
        if (sameCategory) {
            score += 40;
        }

        // 2. Name / Description Word Match (30 points)
        // Uses simple String methods: toLowerCase(), split(" "), and contains()
        String myName = getName().toLowerCase();
        String otherText = (other.getName() + " " + other.getDescription()).toLowerCase();

        boolean nameMatched = false;

        // Check if full item name appears in the other item
        if (otherText.contains(myName) || myName.contains(other.getName().toLowerCase())) {
            nameMatched = true;
        } else {
            // Check individual words from the item name
            String[] words = myName.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i].trim();
                // Check if word has meaningful length (at least 3 characters)
                if (word.length() >= 3 && otherText.contains(word)) {
                    nameMatched = true;
                    break;
                }
            }
        }

        if (nameMatched) {
            score += 30;
        }

        // 3. Location Match (up to 30 points)
        String myLoc = getLocation().toLowerCase();
        String otherLoc = other.getLocation().toLowerCase();
        if (myLoc.equalsIgnoreCase(otherLoc)) {
            score += 30; // Exact location match
        } else if (myLoc.contains(otherLoc) || otherLoc.contains(myLoc)) {
            score += 20; // Partial location match (e.g. "Library" vs "Library 2nd Floor")
        }

        // Safety rule: Two items must share at least one name or description word
        // (An umbrella and a bottle in the same room should not match)
        if (!nameMatched) {
            return 0;
        }

        if (score > 100) {
            score = 100;
        }

        return score;
    }
}
