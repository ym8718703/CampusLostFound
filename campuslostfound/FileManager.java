package campuslostfound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * File management class for reading and writing lost and found records to text files.
 *
 * Demonstrates Syllabus Concepts:
 * - File I/O Operations: Uses FileReader, FileWriter, BufferedReader, BufferedWriter, PrintWriter (Unit II)
 * - Exception Handling: Handles and throws checked IOException (Unit II, CO3)
 * - Collections: Uses List and ArrayList to transfer records (Unit II, CO4)
 */
public class FileManager {

    public static final String DATA_DIR = "data";
    public static final String LOST_FILE = DATA_DIR + File.separator + "lost_items.txt";
    public static final String FOUND_FILE = DATA_DIR + File.separator + "found_items.txt";
    public static final String REPORT_FILE = DATA_DIR + File.separator + "match_report.txt";

    public FileManager() {
        // Ensure data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Saves a list of items to the specified file path.
     */
    public void saveItems(List<? extends Item> items, String path) throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                writer.write(item.toFileString());
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error closing file writer: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads lost items from lost_items.txt.
     */
    public List<LostItem> loadLostItems() throws IOException {
        List<LostItem> list = new ArrayList<>();
        List<String[]> rows = readFields(LOST_FILE);

        for (int i = 0; i < rows.size(); i++) {
            String[] f = rows.get(i);
            try {
                // f: id, name, category, description, location, date, ownerName, ownerContact, status
                LostItem item = new LostItem(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
                list.add(item);
            } catch (InvalidItemException e) {
                System.err.println("Skipped invalid lost item record in file: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Loads found items from found_items.txt.
     */
    public List<FoundItem> loadFoundItems() throws IOException {
        List<FoundItem> list = new ArrayList<>();
        List<String[]> rows = readFields(FOUND_FILE);

        for (int i = 0; i < rows.size(); i++) {
            String[] f = rows.get(i);
            try {
                // f: id, name, category, description, location, date, finderName, finderContact, status
                FoundItem item = new FoundItem(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
                list.add(item);
            } catch (InvalidItemException e) {
                System.err.println("Skipped invalid found item record in file: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Helper method to read delimiter-separated rows from file.
     */
    private List<String[]> readFields(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return rows;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                if (fields.length == 9) {
                    rows.add(fields);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing file reader: " + e.getMessage());
                }
            }
        }
        return rows;
    }

    /**
     * Exports a formatted match report to text file using PrintWriter.
     */
    public void exportMatchReport(List<MatchResult> matches, String path) throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(path));
            out.println("==================================================");
            out.println("      CAMPUS LOST & FOUND - MATCH REPORT");
            out.println("==================================================");
            if (matches.isEmpty()) {
                out.println("No matching records found currently.");
            } else {
                for (int i = 0; i < matches.size(); i++) {
                    MatchResult m = matches.get(i);
                    out.println("Match Score : " + m.getScore() + "%");
                    out.println("LOST ITEM   : " + m.getLostItem().getName() + " [ID: " + m.getLostItem().getId() + "]");
                    out.println("  Owner     : " + m.getLostItem().getPersonName() + " (" + m.getLostItem().getPersonContact() + ")");
                    out.println("  Location  : " + m.getLostItem().getLocation() + " | Date: " + m.getLostItem().getDate());
                    out.println("FOUND ITEM  : " + m.getFoundItem().getName() + " [ID: " + m.getFoundItem().getId() + "]");
                    out.println("  Finder    : " + m.getFoundItem().getPersonName() + " (" + m.getFoundItem().getPersonContact() + ")");
                    out.println("  Location  : " + m.getFoundItem().getLocation() + " | Date: " + m.getFoundItem().getDate());
                    out.println("--------------------------------------------------");
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
