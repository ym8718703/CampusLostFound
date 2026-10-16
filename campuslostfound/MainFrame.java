package campuslostfound;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Simplified Desktop Graphical User Interface (GUI).
 *
 * Demonstrates:
 * - Window and Component Layout (JFrame, JPanel, JTable, JTabbedPane)
 * - Event Handling using classic ActionListener anonymous inner classes (Standard Core Java)
 * - Interaction with business logic layer (ItemManager)
 */
public class MainFrame extends JFrame {

    private static final String[] CATEGORIES = {
            "Electronics", "Wallet", "Books", "ID Card", "Keys",
            "Clothing", "Bag", "Accessories", "Other"
    };

    private static final String[] ITEM_COLUMNS = {
            "ID", "Type", "Item Name", "Category", "Location", "Date", "Reported By", "Contact", "Status"
    };

    private static final String[] MATCH_COLUMNS = {
            "Score", "Lost ID", "Lost Item", "Owner", "Found ID", "Found Item", "Finder", "Found Location"
    };

    private final FileManager fileManager;
    private final ItemManager manager;

    // All Items tab components
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private DefaultTableModel itemsModel;
    private JTable itemsTable;

    // Matches tab components
    private DefaultTableModel matchModel;
    private JTable matchTable;
    private JLabel matchCountLabel;
    private List<MatchResult> currentMatches = new ArrayList<>();

    // Status Bar
    private JLabel statusLabel;

    public MainFrame(ItemManager manager, FileManager fileManager) {
        super("Campus Lost & Found Management System");
        this.manager = manager;
        this.fileManager = fileManager;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Title
        JLabel title = new JLabel("Campus Lost & Found Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Report Lost Item", buildReportPanel(true));
        tabs.addTab("Report Found Item", buildReportPanel(false));
        tabs.addTab("All Items", buildItemsPanel());
        tabs.addTab("Matches", buildMatchPanel());

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refreshAll();
            }
        });
        add(tabs, BorderLayout.CENTER);

        // Footer Status Bar
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        add(statusLabel, BorderLayout.SOUTH);

        refreshAll();
    }

    // =========================================================================
    // Report Form Panel (Lost / Found)
    // =========================================================================
    private JPanel buildReportPanel(final boolean isLost) {
        String role = isLost ? "Owner" : "Finder";

        final JTextField nameField = new JTextField();
        final JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
        final JTextField descField = new JTextField();
        final JTextField locationField = new JTextField();

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        final JTextField dateField = new JTextField(today);
        final JTextField personField = new JTextField();
        final JTextField contactField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 60, 10, 60));

        form.add(new JLabel("Item Name *"));
        form.add(nameField);
        form.add(new JLabel("Category *"));
        form.add(categoryBox);
        form.add(new JLabel("Description (Color, Brand, Marks)"));
        form.add(descField);
        form.add(new JLabel(isLost ? "Place Lost *" : "Place Found *"));
        form.add(locationField);
        form.add(new JLabel("Date (YYYY-MM-DD) *"));
        form.add(dateField);
        form.add(new JLabel(role + " Name *"));
        form.add(personField);
        form.add(new JLabel(role + " Contact (10-digit Phone / Email) *"));
        form.add(contactField);

        JButton submitBtn = new JButton(isLost ? "Submit Lost Report" : "Submit Found Report");
        JButton clearBtn = new JButton("Clear Form");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.add(submitBtn);
        buttons.add(clearBtn);

        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                categoryBox.setSelectedIndex(0);
                descField.setText("");
                locationField.setText("");
                dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                personField.setText("");
                contactField.setText("");
            }
        });

        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String category = (String) categoryBox.getSelectedItem();
                    String id;
                    List<MatchResult> matches;

                    if (isLost) {
                        LostItem item = manager.addLostItem(nameField.getText(), category,
                                descField.getText(), locationField.getText(), dateField.getText(),
                                personField.getText(), contactField.getText());
                        id = item.getId();
                        matches = manager.findMatchesFor(item);
                    } else {
                        FoundItem item = manager.addFoundItem(nameField.getText(), category,
                                descField.getText(), locationField.getText(), dateField.getText(),
                                personField.getText(), contactField.getText());
                        id = item.getId();
                        matches = manager.findMatchesFor(item);
                    }

                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Report saved successfully!\nGenerated ID: " + id,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    if (!matches.isEmpty()) {
                        StringBuilder sb = new StringBuilder("Possible match(es) identified:\n\n");
                        int count = Math.min(matches.size(), 3);
                        for (int i = 0; i < count; i++) {
                            MatchResult m = matches.get(i);
                            sb.append("Match [").append(m.getScore()).append("%]: ")
                              .append(m.getLostItem().getName()).append(" <-> ")
                              .append(m.getFoundItem().getName()).append("\n");
                        }
                        sb.append("\nPlease check the 'Matches' tab to verify.");
                        JOptionPane.showMessageDialog(MainFrame.this, sb.toString(),
                                "Match Alert", JOptionPane.INFORMATION_MESSAGE);
                    }

                    // Reset fields
                    nameField.setText("");
                    descField.setText("");
                    locationField.setText("");
                    personField.setText("");
                    contactField.setText("");
                    refreshAll();

                } catch (InvalidItemException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "File error: " + ex.getMessage(),
                            "Storage Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.add(form, BorderLayout.CENTER);
        container.add(buttons, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(container, BorderLayout.NORTH);
        return panel;
    }

    // =========================================================================
    // All Items Panel
    // =========================================================================
    private JPanel buildItemsPanel() {
        searchField = new JTextField(15);
        String[] filterOptions = new String[CATEGORIES.length + 1];
        filterOptions[0] = "All";
        System.arraycopy(CATEGORIES, 0, filterOptions, 1, CATEGORIES.length);
        categoryFilter = new JComboBox<>(filterOptions);

        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Show All");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(new JLabel("Search Keyword:"));
        top.add(searchField);
        top.add(new JLabel("Category:"));
        top.add(categoryFilter);
        top.add(searchBtn);
        top.add(resetBtn);

        itemsModel = new DefaultTableModel(ITEM_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(22);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton returnBtn = new JButton("Mark as Returned");
        JButton deleteBtn = new JButton("Delete Record");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        bottom.add(returnBtn);
        bottom.add(deleteBtn);

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshItemsTable();
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                categoryFilter.setSelectedIndex(0);
                refreshItemsTable();
            }
        });

        returnBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = itemsTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please select an item from the table first.",
                            "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String id = (String) itemsTable.getValueAt(row, 0);
                try {
                    manager.markReturned(id);
                    JOptionPane.showMessageDialog(MainFrame.this, "Item " + id + " marked as RETURNED.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAll();
                } catch (ItemNotFoundException | IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = itemsTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please select an item from the table first.",
                            "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String id = (String) itemsTable.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Are you sure you want to delete item " + id + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        manager.deleteItem(id);
                        JOptionPane.showMessageDialog(MainFrame.this, "Item " + id + " deleted successfully.",
                                "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        refreshAll();
                    } catch (ItemNotFoundException | IOException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // Matches Panel
    // =========================================================================
    private JPanel buildMatchPanel() {
        JButton findBtn = new JButton("Find Matches");
        JButton confirmBtn = new JButton("Confirm Return (Selected Match)");
        JButton exportBtn = new JButton("Export Report to File");
        matchCountLabel = new JLabel(" ");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        top.add(findBtn);
        top.add(confirmBtn);
        top.add(exportBtn);
        top.add(matchCountLabel);

        matchModel = new DefaultTableModel(MATCH_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        matchTable = new JTable(matchModel);
        matchTable.setRowHeight(22);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        findBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshMatches();
            }
        });

        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = matchTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please select a match from the table first.",
                            "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    MatchResult m = currentMatches.get(row);
                    manager.confirmMatch(m);
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Match confirmed! Both lost item (" + m.getLostItem().getId()
                            + ") and found item (" + m.getFoundItem().getId() + ") marked as RETURNED.",
                            "Item Claimed", JOptionPane.INFORMATION_MESSAGE);
                    refreshAll();
                } catch (ItemNotFoundException | IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileManager.exportMatchReport(currentMatches, FileManager.REPORT_FILE);
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Report successfully exported to: " + FileManager.REPORT_FILE,
                            "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Export error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(matchTable), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // Refresh Logic
    // =========================================================================
    public void refreshAll() {
        refreshItemsTable();
        refreshMatches();
        statusLabel.setText("Total Lost: " + manager.getLostCount()
                + "  |  Total Found: " + manager.getFoundCount()
                + "  |  Returned / Claimed: " + manager.getReturnedCount());
    }

    private void refreshItemsTable() {
        itemsModel.setRowCount(0);
        String keyword = (searchField != null) ? searchField.getText() : "";
        String cat = (categoryFilter != null) ? (String) categoryFilter.getSelectedItem() : "All";
        List<Item> items = manager.search(keyword, cat);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            itemsModel.addRow(new Object[] {
                    item.getId(),
                    item.getType(),
                    item.getName(),
                    item.getCategory(),
                    item.getLocation(),
                    item.getDate(),
                    item.getPersonName() + " (" + item.getPersonRole() + ")",
                    item.getPersonContact(),
                    item.getStatus()
            });
        }
    }

    private void refreshMatches() {
        currentMatches = manager.findAllMatches();
        matchModel.setRowCount(0);
        for (int i = 0; i < currentMatches.size(); i++) {
            MatchResult m = currentMatches.get(i);
            matchModel.addRow(new Object[] {
                    m.getScore() + "%",
                    m.getLostItem().getId(),
                    m.getLostItem().getName(),
                    m.getLostItem().getPersonName(),
                    m.getFoundItem().getId(),
                    m.getFoundItem().getName(),
                    m.getFoundItem().getPersonName(),
                    m.getFoundItem().getLocation()
            });
        }
        if (matchCountLabel != null) {
            matchCountLabel.setText(currentMatches.size() + " match(es) identified");
        }
    }
}
