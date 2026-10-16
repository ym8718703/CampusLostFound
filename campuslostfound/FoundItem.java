package campuslostfound;

/**
 * Subclass representing an item reported as found on campus.
 *
 * Demonstrates Syllabus Concepts:
 * - Inheritance: Subclass of abstract Item class (Unit I, CO2)
 * - Polymorphism: Overriding abstract methods of superclass (Unit I, CO2)
 */
public class FoundItem extends Item {

    /**
     * Parameterized Constructor invoking super(...)
     */
    public FoundItem(String id, String name, String category, String description,
                     String location, String date, String finderName,
                     String finderContact, String status) throws InvalidItemException {
        super(id, name, category, description, location, date, finderName, finderContact, status);
    }

    @Override
    public String getType() {
        return "FOUND";
    }

    @Override
    public String getPersonRole() {
        return "Finder";
    }

    @Override
    public void displayDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("FOUND ITEM RECORD: " + getId());
        System.out.println("   Item Name    : " + getName());
        System.out.println("   Category     : " + getCategory());
        System.out.println("   Description  : " + getDescription());
        System.out.println("   Place Found  : " + getLocation());
        System.out.println("   Date Found   : " + getDate());
        System.out.println("   Finder Name  : " + getPersonName());
        System.out.println("   Contact      : " + getPersonContact());
        System.out.println("   Status       : " + getStatus());
        System.out.println("--------------------------------------------------");
    }
}
