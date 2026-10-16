package campuslostfound;

import java.io.IOException;
import java.util.List;

/**
 * Background worker thread that scans items and updates match records.
 *
 * Demonstrates Syllabus Concepts:
 * - Multithreading: Extends java.lang.Thread class and overrides run() (Unit II, CO4, Exp 5)
 * - Thread Synchronization: Uses synchronized block on shared ItemManager instance to prevent race conditions (Unit II, CO4, Exp 5)
 * - Thread Control: Uses Thread.sleep() and Thread.currentThread() methods (Unit II, CO4)
 */
public class AutoMatchThread extends Thread {

    private final ItemManager manager;
    private final FileManager fileManager;

    public AutoMatchThread(ItemManager manager, FileManager fileManager) {
        super("AutoMatchWorkerThread");
        this.manager = manager;
        this.fileManager = fileManager;
    }

    @Override
    public void run() {
        System.out.println("\n[" + getName() + "] Background thread started.");
        try {
            System.out.println("[" + getName() + "] Step 1: Scanning lost and found item collections...");
            Thread.sleep(600); // Simulate processing time

            List<MatchResult> matches;

            // Synchronized block to ensure thread-safe access to collections
            synchronized (manager) {
                System.out.println("[" + getName() + "] Step 2: Acquired lock on ItemManager. Computing match scores...");
                matches = manager.findAllMatches();
            }

            Thread.sleep(400); // Simulate background export

            System.out.println("[" + getName() + "] Step 3: Found " + matches.size() + " potential match(es).");

            // Exporting to report file in the background
            try {
                fileManager.exportMatchReport(matches, FileManager.REPORT_FILE);
                System.out.println("[" + getName() + "] Step 4: Successfully saved background report to: " + FileManager.REPORT_FILE);
            } catch (IOException e) {
                System.err.println("[" + getName() + "] Error saving background report: " + e.getMessage());
            }

            System.out.println("[" + getName() + "] Background synchronization completed successfully!\n");

        } catch (InterruptedException e) {
            System.err.println("[" + getName() + "] Thread was interrupted: " + e.getMessage());
        }
    }
}
