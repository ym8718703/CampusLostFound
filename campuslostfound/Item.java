package campuslostfound;

/**
 * Abstract Base Class representing a campus lost or found item.
 *
 * Demonstrates Syllabus Concepts:
 * - Abstraction: Abstract class and abstract methods (Unit I, CO2)
 * - Encapsulation: Private member variables with public getters and validated setters (Unit I, CO2)
 * - Polymorphism: Method Overloading and Method Overriding (Unit I, CO2)
 * - String Processing: Standard string functions length(), trim(), toLowerCase(), split(), indexOf() (Unit II, CO3)
 * - Exception Handling: Throws user-defined InvalidItemException on validation failure (Unit II, CO3)
 */
public abstract class Item implements Storable {

    // Status Constants
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_RETURNED = "RETURNED";

    // Encapsulated Fields (Private access modifier)
    private String id;
    private String name;
    private String category;
    private String description;
    private String location;
    private String date;            // Stored in YYYY-MM-DD format
    private String personName;      // Owner (for lost) or Finder (for found)
    private String personContact;   // Phone number or email
    private String status;          // "OPEN" or "RETURNED"

    /**
     * Parameterized Constructor
     */
    public Item(String id, String name, String category, String description,
                String location, String date, String personName,
                String personContact, String status) throws InvalidItemException {
        this.id = id;
        setName(name);
        setCategory(category);
        setDescription(description);
        setLocation(location);
        setDate(date);
        setPersonName(personName);
        setPersonContact(personContact);
        setStatus(status);
    }

    // =========================================================================
    // Abstract Methods (Must be implemented by subclasses - Abstraction)
    // =========================================================================
    public abstract String getType();          // "LOST" or "FOUND"
    public abstract String getPersonRole();    // "Owner" or "Finder"
    public abstract void displayDetails();     // Polymorphic display

    // =========================================================================
    // Method Overloading (Compile-time Polymorphism - Unit I, CO2)
    // =========================================================================
    /**
     * Overloaded displayDetails method allowing caller to specify whether
     * contact information should be shown.
     */
    public void displayDetails(boolean showContact) {
        displayDetails();
        if (showContact) {
            System.out.println("   Contact: " + personContact);
        }
    }

    // =========================================================================
    // Validation Helper Methods (String Handling - Unit II, CO3)
    // =========================================================================
    private static String validateNonEmpty(String value, String fieldName) throws InvalidItemException {
        if (value == null || value.trim().length() == 0) {
            throw new InvalidItemException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getCategory()      { return category; }
    public String getDescription()   { return description; }
    public String getLocation()      { return location; }
    public String getDate()          { return date; }
    public String getPersonName()    { return personName; }
    public String getPersonContact() { return personContact; }
    public String getStatus()        { return status; }
    public boolean isOpen()          { return STATUS_OPEN.equalsIgnoreCase(status); }

    // =========================================================================
    // Setters with Validation
    // =========================================================================
    public void setId(String id) {
        this.id = id;
    }

    public final void setName(String name) throws InvalidItemException {
        this.name = validateNonEmpty(name, "Item name");
    }

    public final void setCategory(String category) throws InvalidItemException {
        this.category = validateNonEmpty(category, "Category");
    }

    public final void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public final void setLocation(String location) throws InvalidItemException {
        this.location = validateNonEmpty(location, "Location");
    }

    /**
     * Validates date format (YYYY-MM-DD) using basic string handling.
     */
    public final void setDate(String dateStr) throws InvalidItemException {
        String cleanDate = validateNonEmpty(dateStr, "Date");
        String[] parts = cleanDate.split("-");
        boolean valid = false;

        if (parts.length == 3) {
            try {
                int year = Integer.parseInt(parts[0].trim());
                int month = Integer.parseInt(parts[1].trim());
                int day = Integer.parseInt(parts[2].trim());

                if (year >= 2000 && year <= 2099 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        if (!valid) {
            throw new InvalidItemException("Date must be in YYYY-MM-DD format (e.g., 2026-10-15).");
        }
        this.date = cleanDate;
    }

    public final void setPersonName(String personName) throws InvalidItemException {
        this.personName = validateNonEmpty(personName, "Owner / Finder name");
    }

    /**
     * Validates contact details (10-digit phone or valid email format)
     * using standard String class methods.
     */
    public final void setPersonContact(String contact) throws InvalidItemException {
        String c = validateNonEmpty(contact, "Contact");

        // Check if 10-digit numeric phone number
        boolean isPhone = false;
        if (c.length() == 10) {
            isPhone = true;
            for (int i = 0; i < c.length(); i++) {
                if (!Character.isDigit(c.charAt(i))) {
                    isPhone = false;
                    break;
                }
            }
        }

        // Check if email address using standard String methods (indexOf)
        boolean isEmail = false;
        int atIndex = c.indexOf('@');
        int dotIndex = c.lastIndexOf('.');
        if (atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < c.length() - 1) {
            isEmail = true;
        }

        if (!isPhone && !isEmail) {
            throw new InvalidItemException("Contact must be a 10-digit phone number or a valid email address.");
        }
        this.personContact = c;
    }

    public final void setStatus(String status) throws InvalidItemException {
        if (!STATUS_OPEN.equalsIgnoreCase(status) && !STATUS_RETURNED.equalsIgnoreCase(status)) {
            throw new InvalidItemException("Status must be OPEN or RETURNED.");
        }
        this.status = status.toUpperCase();
    }

    /**
     * Replaces file delimiter characters to maintain storage integrity.
     */
    protected static String cleanDelimiter(String s) {
        if (s == null) return "";
        return s.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    @Override
    public String toFileString() {
        return id + "|" + cleanDelimiter(name) + "|" + cleanDelimiter(category) + "|"
                + cleanDelimiter(description) + "|" + cleanDelimiter(location) + "|"
                + date + "|" + cleanDelimiter(personName) + "|"
                + cleanDelimiter(personContact) + "|" + status;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + category + ", " + location + ", " + date + ") - Status: " + status;
    }
}
