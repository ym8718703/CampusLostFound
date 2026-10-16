package campuslostfound;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Main application entry point for the Campus Lost & Found System.
 *
 * Demonstrates Syllabus Concepts:
 * - Fundamental Programming & Control Structures (Unit I, CO1): switch-case, while loops, methods
 * - Object-Oriented Programming (Unit I, CO2): Classes, Objects, Inheritance, Polymorphism, Packages
 * - String Processing & Exception Handling (Unit II, CO3): Validating user input, handling custom exceptions
 * - Collections Framework (Unit II, CO4): ArrayList, HashMap, HashSet iteration and usage
 * - Multithreading & Synchronization (Unit II, CO4, Exp 5): Starting background AutoMatchThread
 */
public class Main {

    private static final FileManager fileManager = new FileManager();
    private static ItemManager itemManager;

    public static void main(String[] args) {
        // Initialize ItemManager and load saved records
        try {
            itemManager = new ItemManager(fileManager);
        } catch (IOException e) {
            System.err.println("Fatal Error loading data files: " + e.getMessage());
            return;
        }

        // If user runs with "--gui" flag, launch GUI directly
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            launchGUI();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=============================================================");
        System.out.println("   WELCOME TO CAMPUS LOST & FOUND MANAGEMENT SYSTEM");
        System.out.println("   (CB2206L: Skill Development Lab-I Mini Project)");
        System.out.println("=============================================================");

        while (running) {
            printMenu();
            System.out.print("Enter your choice (0 - 10): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleReportLostItem(scanner);
                    break;
                case "2":
                    handleReportFoundItem(scanner);
                    break;
                case "3":
                    handleViewAllItems();
                    break;
                case "4":
                    handleSearchItems(scanner);
                    break;
                case "5":
                    handleFindMatches();
                    break;
                case "6":
                    handleMarkReturned(scanner);
                    break;
                case "7":
                    handleDeleteItem(scanner);
                    break;
                case "8":
                    handleViewCategoriesAndStats();
                    break;
                case "9":
                    handleRunMultithreadedSync();
                    break;
                case "10":
                    launchGUI();
                    break;
                case "0":
                    System.out.println("\nThank you for using Campus Lost & Found Management System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice! Please select an option between 0 and 10.\n");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("                    MAIN MENU OPTIONS");
        System.out.println("-------------------------------------------------------------");
        System.out.println(" 1. Report a Lost Item");
        System.out.println(" 2. Report a Found Item");
        System.out.println(" 3. View All Registered Items");
        System.out.println(" 4. Search Items (Demonstrates Method Overloading)");
        System.out.println(" 5. Find Matches (Lost vs Found)");
        System.out.println(" 6. Mark Item as Claimed / Returned");
        System.out.println(" 7. Delete an Item Record");
        System.out.println(" 8. View Categories & Statistics (Demonstrates HashSet & HashMap)");
        System.out.println(" 9. Run Background Auto-Match Worker (Demonstrates Multithreading)");
        System.out.println(" 10. Launch Graphical User Interface (Swing GUI)");
        System.out.println(" 0. Exit System");
        System.out.println("-------------------------------------------------------------");
    }

    // =========================================================================
    // 1. Report Lost Item
    // =========================================================================
    private static void handleReportLostItem(Scanner sc) {
        System.out.println("\n>>> REPORT LOST ITEM <<<");
        try {
            System.out.print("Enter Item Name (e.g., Casio Calculator): ");
            String name = sc.nextLine();

            System.out.print("Enter Category (Electronics/Wallet/Books/ID Card/Keys/Clothing/Other): ");
            String category = sc.nextLine();

            System.out.print("Enter Description (color, brand, marks): ");
            String desc = sc.nextLine();

            System.out.print("Enter Place Lost (e.g., Library 2nd Floor): ");
            String location = sc.nextLine();

            String defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            System.out.print("Enter Date Lost (YYYY-MM-DD) [Press Enter for today: " + defaultDate + "]: ");
            String date = sc.nextLine().trim();
            if (date.length() == 0) {
                date = defaultDate;
            }

            System.out.print("Enter Owner Name: ");
            String ownerName = sc.nextLine();

            System.out.print("Enter Owner Contact (10-digit Phone or Email): ");
            String contact = sc.nextLine();

            LostItem lost = itemManager.addLostItem(name, category, desc, location, date, ownerName, contact);
            System.out.println("\n[SUCCESS] Lost item registered successfully with ID: " + lost.getId());

            // Check immediate matches
            List<MatchResult> matches = itemManager.findMatchesFor(lost);
            if (!matches.isEmpty()) {
                System.out.println("[MATCH ALERT] " + matches.size() + " potential matching item(s) found in repository!");
                for (int i = 0; i < matches.size(); i++) {
                    MatchResult m = matches.get(i);
                    System.out.println("   -> Match Score: " + m.getScore() + "% | Found Item: "
                            + m.getFoundItem().getName() + " [ID: " + m.getFoundItem().getId() + "] at "
                            + m.getFoundItem().getLocation());
                }
            }

        } catch (InvalidItemException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n[FILE ERROR] Could not save record: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. Report Found Item
    // =========================================================================
    private static void handleReportFoundItem(Scanner sc) {
        System.out.println("\n>>> REPORT FOUND ITEM <<<");
        try {
            System.out.print("Enter Item Name (e.g., Blue Water Bottle): ");
            String name = sc.nextLine();

            System.out.print("Enter Category (Electronics/Wallet/Books/ID Card/Keys/Clothing/Other): ");
            String category = sc.nextLine();

            System.out.print("Enter Description (color, brand, marks): ");
            String desc = sc.nextLine();

            System.out.print("Enter Place Found (e.g., Canteen): ");
            String location = sc.nextLine();

            String defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            System.out.print("Enter Date Found (YYYY-MM-DD) [Press Enter for today: " + defaultDate + "]: ");
            String date = sc.nextLine().trim();
            if (date.length() == 0) {
                date = defaultDate;
            }

            System.out.print("Enter Finder Name: ");
            String finderName = sc.nextLine();

            System.out.print("Enter Finder Contact (10-digit Phone or Email): ");
            String contact = sc.nextLine();

            FoundItem found = itemManager.addFoundItem(name, category, desc, location, date, finderName, contact);
            System.out.println("\n[SUCCESS] Found item registered successfully with ID: " + found.getId());

            // Check immediate matches
            List<MatchResult> matches = itemManager.findMatchesFor(found);
            if (!matches.isEmpty()) {
                System.out.println("[MATCH ALERT] " + matches.size() + " potential matching lost item(s) found!");
                for (int i = 0; i < matches.size(); i++) {
                    MatchResult m = matches.get(i);
                    System.out.println("   -> Match Score: " + m.getScore() + "% | Lost Item: "
                            + m.getLostItem().getName() + " [ID: " + m.getLostItem().getId() + "] Owner: "
                            + m.getLostItem().getPersonName());
                }
            }

        } catch (InvalidItemException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n[FILE ERROR] Could not save record: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. View All Items
    // =========================================================================
    private static void handleViewAllItems() {
        System.out.println("\n>>> ALL REGISTERED ITEMS (Polymorphism: displayDetails()) <<<");
        List<Item> all = itemManager.getAllItems();
        if (all.isEmpty()) {
            System.out.println("No items registered in the system yet.");
            return;
        }

        for (int i = 0; i < all.size(); i++) {
            // Polymorphic method call - invokes either LostItem or FoundItem displayDetails()
            all.get(i).displayDetails();
        }
    }

    // =========================================================================
    // 4. Search Items (Demonstrating Method Overloading)
    // =========================================================================
    private static void handleSearchItems(Scanner sc) {
        System.out.println("\n>>> SEARCH ITEMS (Method Overloading Demo) <<<");
        System.out.println("1. Search by Keyword only: search(keyword)");
        System.out.println("2. Search by Keyword AND Category: search(keyword, category)");
        System.out.print("Choose search mode (1 or 2): ");
        String mode = sc.nextLine().trim();

        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine();

        List<Item> results;
        if ("2".equals(mode)) {
            System.out.print("Enter category filter: ");
            String category = sc.nextLine();
            // Invokes overloaded method: search(String keyword, String category)
            results = itemManager.search(keyword, category);
            System.out.println("\n[Search with Category Filter: '" + category + "']");
        } else {
            // Invokes method: search(String keyword)
            results = itemManager.search(keyword);
            System.out.println("\n[General Keyword Search]");
        }

        if (results.isEmpty()) {
            System.out.println("No items matched your search criteria.");
        } else {
            System.out.println("Found " + results.size() + " matching item(s):");
            for (int i = 0; i < results.size(); i++) {
                results.get(i).displayDetails();
            }
        }
    }

    // =========================================================================
    // 5. Find Matches
    // =========================================================================
    private static void handleFindMatches() {
        System.out.println("\n>>> MATCHING LOST & FOUND RECORDS <<<");
        List<MatchResult> matches = itemManager.findAllMatches();

        if (matches.isEmpty()) {
            System.out.println("No matches found currently between open lost and found items.");
            return;
        }

        System.out.println("Identified " + matches.size() + " potential match(es) (sorted by match score):");
        for (int i = 0; i < matches.size(); i++) {
            MatchResult m = matches.get(i);
            System.out.println("--------------------------------------------------");
            System.out.println("Match #" + (i + 1) + " | Confidence: " + m.getScore() + "%");
            System.out.println("  LOST  : [" + m.getLostItem().getId() + "] " + m.getLostItem().getName()
                    + " | Owner: " + m.getLostItem().getPersonName() + " (" + m.getLostItem().getPersonContact() + ")");
            System.out.println("  FOUND : [" + m.getFoundItem().getId() + "] " + m.getFoundItem().getName()
                    + " | Finder: " + m.getFoundItem().getPersonName() + " (" + m.getFoundItem().getPersonContact() + ")");
            System.out.println("  Location Match: " + m.getLostItem().getLocation() + " <-> " + m.getFoundItem().getLocation());
        }
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // 6. Mark Item as Claimed / Returned
    // =========================================================================
    private static void handleMarkReturned(Scanner sc) {
        System.out.println("\n>>> MARK ITEM AS CLAIMED / RETURNED <<<");
        System.out.print("Enter Item ID to mark as RETURNED (e.g., L001 or F001): ");
        String id = sc.nextLine().trim();

        try {
            itemManager.markReturned(id);
            System.out.println("[SUCCESS] Item " + id + " has been marked as RETURNED.");
        } catch (ItemNotFoundException e) {
            System.out.println("[NOT FOUND] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not update record: " + e.getMessage());
        }
    }

    // =========================================================================
    // 7. Delete Item
    // =========================================================================
    private static void handleDeleteItem(Scanner sc) {
        System.out.println("\n>>> DELETE ITEM RECORD <<<");
        System.out.print("Enter Item ID to delete permanently: ");
        String id = sc.nextLine().trim();

        System.out.print("Are you sure you want to delete item " + id + "? (y/n): ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion canceled.");
            return;
        }

        try {
            itemManager.deleteItem(id);
            System.out.println("[SUCCESS] Item " + id + " deleted successfully.");
        } catch (ItemNotFoundException e) {
            System.out.println("[NOT FOUND] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not delete record: " + e.getMessage());
        }
    }

    // =========================================================================
    // 8. Categories & System Statistics (Demonstrating HashSet & HashMap)
    // =========================================================================
    private static void handleViewCategoriesAndStats() {
        System.out.println("\n>>> SYSTEM STATISTICS & COLLECTIONS OVERVIEW <<<");
        System.out.println("Total Registered Lost Items  : " + itemManager.getLostCount());
        System.out.println("Total Registered Found Items : " + itemManager.getFoundCount());
        System.out.println("Total Returned / Claimed     : " + itemManager.getReturnedCount());

        // Demonstrating HashSet usage
        Set<String> uniqueCategories = itemManager.getAllCategories();
        System.out.println("\nUnique Item Categories in Inventory (Stored in HashSet):");
        for (String cat : uniqueCategories) {
            System.out.println("   * " + cat);
        }
    }

    // =========================================================================
    // 9. Multithreaded Background Sync (Demonstrating Multithreading & Synchronization)
    // =========================================================================
    private static void handleRunMultithreadedSync() {
        System.out.println("\n>>> LAUNCHING MULTITHREADED BACKGROUND WORKER <<<");
        System.out.println("Creating and starting AutoMatchThread (extends Thread)...");

        AutoMatchThread thread = new AutoMatchThread(itemManager, fileManager);
        thread.start();

        try {
            // Wait for thread to finish so output does not collide with menu prompt
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted while waiting for worker: " + e.getMessage());
        }
    }

    // =========================================================================
    // 10. Launch Graphical User Interface
    // =========================================================================
    private static void launchGUI() {
        System.out.println("\n[Launching Swing Graphical User Interface...]");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame(itemManager, fileManager);
                frame.setVisible(true);
            }
        });
    }
}
