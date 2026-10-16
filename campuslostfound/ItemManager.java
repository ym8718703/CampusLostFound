package campuslostfound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller class managing the lost and found inventory and matching logic.
 *
 * Demonstrates Syllabus Concepts:
 * - Collections Framework: ArrayList (lists of items), HashMap (lookup by ID), HashSet (unique categories) (Unit II, CO4)
 * - Polymorphism: Method Overloading for search() and findMatchesFor() (Unit I, CO2)
 * - Exception Handling: Throws and handles InvalidItemException and ItemNotFoundException (Unit II, CO3)
 * - Thread Synchronization: Uses synchronized methods to guarantee thread safety during concurrent access (Unit II, CO4)
 */
public class ItemManager {

    // Collections
    private final List<LostItem> lostItems = new ArrayList<>();
    private final List<FoundItem> foundItems = new ArrayList<>();
    private final Map<String, Item> itemMap = new HashMap<>(); // Fast O(1) lookup by ID
    private final FileManager fileManager;

    private int lostCounter = 0;
    private int foundCounter = 0;

    public ItemManager(FileManager fileManager) throws IOException {
        this.fileManager = fileManager;
        loadData();
    }

    // =========================================================================
    // Data Loading & Persistence
    // =========================================================================
    private synchronized void loadData() throws IOException {
        List<LostItem> loadedLost = fileManager.loadLostItems();
        for (int i = 0; i < loadedLost.size(); i++) {
            LostItem item = loadedLost.get(i);
            lostItems.add(item);
            itemMap.put(item.getId(), item);
            updateLostCounter(item.getId());
        }

        List<FoundItem> loadedFound = fileManager.loadFoundItems();
        for (int i = 0; i < loadedFound.size(); i++) {
            FoundItem item = loadedFound.get(i);
            foundItems.add(item);
            itemMap.put(item.getId(), item);
            updateFoundCounter(item.getId());
        }
    }

    private void updateLostCounter(String id) {
        try {
            int num = Integer.parseInt(id.substring(1));
            if (num > lostCounter) {
                lostCounter = num;
            }
        } catch (Exception ignored) {
        }
    }

    private void updateFoundCounter(String id) {
        try {
            int num = Integer.parseInt(id.substring(1));
            if (num > foundCounter) {
                foundCounter = num;
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized void saveAll() throws IOException {
        fileManager.saveItems(lostItems, FileManager.LOST_FILE);
        fileManager.saveItems(foundItems, FileManager.FOUND_FILE);
    }

    // =========================================================================
    // Adding Items
    // =========================================================================
    public synchronized LostItem addLostItem(String name, String category, String description,
                                            String location, String date, String ownerName,
                                            String ownerContact) throws InvalidItemException, IOException {
        lostCounter++;
        String id = "L" + String.format("%03d", lostCounter);
        LostItem item = new LostItem(id, name, category, description, location, date,
                ownerName, ownerContact, Item.STATUS_OPEN);
        lostItems.add(item);
        itemMap.put(id, item);
        saveAll();
        return item;
    }

    public synchronized FoundItem addFoundItem(String name, String category, String description,
                                              String location, String date, String finderName,
                                              String finderContact) throws InvalidItemException, IOException {
        foundCounter++;
        String id = "F" + String.format("%03d", foundCounter);
        FoundItem item = new FoundItem(id, name, category, description, location, date,
                finderName, finderContact, Item.STATUS_OPEN);
        foundItems.add(item);
        itemMap.put(id, item);
        saveAll();
        return item;
    }

    // =========================================================================
    // Searching Items (Method Overloading - Unit I, CO2)
    // =========================================================================
    /**
     * Search version 1: Search all items matching a keyword in name, description, location, or person.
     */
    public List<Item> search(String keyword) {
        return search(keyword, "All");
    }

    /**
     * Search version 2 (Overloaded): Search items by keyword AND category filter.
     */
    public List<Item> search(String keyword, String category) {
        String key = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<Item> results = new ArrayList<>();
        List<Item> all = getAllItems();

        for (int i = 0; i < all.size(); i++) {
            Item item = all.get(i);
            boolean categoryMatches = category.equalsIgnoreCase("All") || item.getCategory().equalsIgnoreCase(category);

            String combinedText = (item.getName() + " " + item.getDescription() + " " + item.getLocation()
                    + " " + item.getPersonName() + " " + item.getId()).toLowerCase();

            if (categoryMatches && (key.length() == 0 || combinedText.contains(key))) {
                results.add(item);
            }
        }
        return results;
    }

    // =========================================================================
    // Matching Logic (Method Overloading - Unit I, CO2)
    // =========================================================================
    /**
     * Compares every open lost item with every open found item and returns ranked matches.
     */
    public synchronized List<MatchResult> findAllMatches() {
        List<MatchResult> results = new ArrayList<>();
        for (int i = 0; i < lostItems.size(); i++) {
            LostItem lost = lostItems.get(i);
            if (lost.isOpen()) {
                results.addAll(findMatchesFor(lost));
            }
        }
        Collections.sort(results); // Sorts using MatchResult.compareTo (descending order)
        return results;
    }

    /**
     * Overloaded findMatchesFor: Finds matches for a specific LostItem.
     */
    public List<MatchResult> findMatchesFor(LostItem lost) {
        List<MatchResult> results = new ArrayList<>();
        if (lost == null || !lost.isOpen()) {
            return results;
        }

        for (int i = 0; i < foundItems.size(); i++) {
            FoundItem found = foundItems.get(i);
            if (!found.isOpen()) {
                continue;
            }
            int score = lost.calculateMatchScore(found);
            if (score >= Matchable.MATCH_THRESHOLD) {
                results.add(new MatchResult(lost, found, score));
            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Overloaded findMatchesFor: Finds matches for a specific FoundItem.
     */
    public List<MatchResult> findMatchesFor(FoundItem found) {
        List<MatchResult> results = new ArrayList<>();
        if (found == null || !found.isOpen()) {
            return results;
        }

        for (int i = 0; i < lostItems.size(); i++) {
            LostItem lost = lostItems.get(i);
            if (!lost.isOpen()) {
                continue;
            }
            int score = lost.calculateMatchScore(found);
            if (score >= Matchable.MATCH_THRESHOLD) {
                results.add(new MatchResult(lost, found, score));
            }
        }
        Collections.sort(results);
        return results;
    }

    // =========================================================================
    // Item Status Update and Deletion
    // =========================================================================
    public Item getItemById(String id) throws ItemNotFoundException {
        if (id == null) {
            throw new ItemNotFoundException("null");
        }
        Item item = itemMap.get(id.trim());
        if (item == null) {
            throw new ItemNotFoundException(id.trim());
        }
        return item;
    }

    public synchronized void markReturned(String id) throws ItemNotFoundException, IOException {
        Item item = getItemById(id);
        try {
            item.setStatus(Item.STATUS_RETURNED);
        } catch (InvalidItemException e) {
            // Will not happen as STATUS_RETURNED is valid
        }
        saveAll();
    }

    public synchronized void confirmMatch(MatchResult match) throws ItemNotFoundException, IOException {
        markReturned(match.getLostItem().getId());
        markReturned(match.getFoundItem().getId());
    }

    public synchronized void deleteItem(String id) throws ItemNotFoundException, IOException {
        Item item = getItemById(id);
        lostItems.remove(item);
        foundItems.remove(item);
        itemMap.remove(id.trim());
        saveAll();
    }

    // =========================================================================
    // Accessors & Statistics (Demonstrating Collections)
    // =========================================================================
    public List<Item> getAllItems() {
        List<Item> all = new ArrayList<>();
        all.addAll(lostItems);
        all.addAll(foundItems);
        return all;
    }

    public List<LostItem> getLostItems() {
        return lostItems;
    }

    public List<FoundItem> getFoundItems() {
        return foundItems;
    }

    /**
     * Demonstrates HashSet: Collects and returns all unique categories across all items.
     */
    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        List<Item> all = getAllItems();
        for (int i = 0; i < all.size(); i++) {
            categories.add(all.get(i).getCategory());
        }
        return categories;
    }

    public int getLostCount() {
        return lostItems.size();
    }

    public int getFoundCount() {
        return foundItems.size();
    }

    public int getReturnedCount() {
        int count = 0;
        for (Item item : itemMap.values()) {
            if (!item.isOpen()) {
                count++;
            }
        }
        return count;
    }
}
