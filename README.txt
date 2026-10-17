==================================================================
Campus Lost & Found Management System
Course: CB2206L - Skill Development Lab-I (Pattern 2025-26)
Department: Computer Science and Business Systems
JSPM's Rajarshi Shahu College of Engineering, Pune
==================================================================

1. HOW TO RUN
------------------------------------------------------------------
Command Line (Terminal / PowerShell / CMD):
  1. Open a terminal in this project root directory.
  2. Compile:
       javac campuslostfound/*.java
  3. Run (Console Menu-Driven by default):
       java campuslostfound.Main
  4. Run (Direct GUI):
       java campuslostfound.Main --gui
     (or select Option 10 from the console menu)

Windows Shortcut:
  Double-click "run.bat" to compile and start the console application.

Report Generation:
  Run "python generate_report.py" to recreate the Word document report
  (Campus_Lost_Found_Mini_Project_Report.docx).

IDE (Eclipse / IntelliJ / NetBeans):
  - Open or import the folder "CampusLostFound" as an existing project.
  - Set working directory to project root (so it locates the "data" folder).
  - Run "campuslostfound.Main".

------------------------------------------------------------------
2. SYLLABUS MAPPING (Strictly Aligned with CB2206L Lab Syllabus)
------------------------------------------------------------------
[CO2] Object-Oriented Programming:
  - User-Defined Packages: package campuslostfound
  - Access Modifiers: private fields, public getters, protected helper methods.
  - Classes and Objects: Item, LostItem, FoundItem, ItemManager, MatchResult.
  - Constructors: Parameterized constructors & super() constructor chaining.
  - Inheritance: Item (abstract superclass) -> LostItem, FoundItem (subclasses).
  - Abstraction:
      * Abstract Class: Item with abstract methods getType(), getPersonRole(), displayDetails()
      * Interfaces: Storable (toFileString), Matchable (calculateMatchScore)
  - Polymorphism:
      * Method Overloading (Compile-time):
          - Item.displayDetails() and Item.displayDetails(boolean showContact)
          - ItemManager.search(String keyword) and ItemManager.search(String keyword, String category)
          - ItemManager.findMatchesFor(LostItem) and ItemManager.findMatchesFor(FoundItem)
      * Method Overriding (Runtime):
          - getType(), getPersonRole(), displayDetails(), calculateMatchScore(), toString(), toFileString()

[CO3] String Processing & Exception Handling:
  - String Processing:
      * Standard methods: length(), trim(), toLowerCase(), split(" "), indexOf(),
        lastIndexOf(), substring(), replace(), equals(), equalsIgnoreCase(), contains()
      * Contact validation: 10-digit number and email validation via String methods.
      * Date validation: YYYY-MM-DD format parsed using String.split("-") and Integer.parseInt().
      * Simple Matching: Uses equalsIgnoreCase() for category, split(" ") and contains()
        for name words, and equalsIgnoreCase()/contains() for location.
  - Exception Handling:
      * Custom Exceptions: InvalidItemException, ItemNotFoundException (extending Exception)
      * try-catch-finally blocks, throw, throws for file I/O and validation errors.

[CO4] Collections Framework & Multithreading:
  - Java Collections Framework:
      * ArrayList: ArrayList<LostItem>, ArrayList<FoundItem>, ArrayList<Item>, ArrayList<MatchResult>
      * HashMap: Map<String, Item> for O(1) fast item lookup by ID.
      * HashSet: Set<String> in ItemManager to track unique inventory categories.
      * Sorting: Collections.sort() using Comparable<MatchResult> (compareTo).
  - Multithreading & Synchronization (Exp 5 & CO4):
      * AutoMatchThread extends Thread class, implements run() method.
      * Thread Synchronization: Uses synchronized block on ItemManager
        to safely access collections and update files.
      * Thread control: Thread.sleep() and Thread.currentThread().

------------------------------------------------------------------
3. OUT-OF-SYLLABUS CONCEPTS REMOVED FOR SUBMISSION
------------------------------------------------------------------
The following non-syllabus concepts were removed and replaced with simple Java:
  [X] java.time.LocalDate / ChronoUnit / DateTimeParseException
      -> Replaced with syllabus-compliant standard String date validation.
  [X] Java 8 Lambda Expressions (() -> ... and e -> ...)
      -> Replaced with standard core Java Scanner flow and classic ActionListener inner classes.
  [X] Java 8 Stream API & Method References (Item::getDate)
      -> Replaced with standard Core Java for-loops and Collections.sort().
  [X] Complex Regular Expressions (e.g. \\d{10}, [\\w.+-]+@[\\w-]+..., [ ,;:.!?()/\"]+)
      -> Replaced with String methods (charAt, isDigit, indexOf, split(" ")).
  [X] Complex Stop-Words NLP Logic
      -> Replaced with simple Java String split(" ") and contains() matching.
  [X] Nimbus Look-and-Feel Reflection Loops
      -> Removed; now uses standard clean UI components.

------------------------------------------------------------------
4. PROJECT STRUCTURE
------------------------------------------------------------------
CampusLostFound/
  +-- campuslostfound/
  |     +-- Item.java                    (Abstract base class)
  |     +-- LostItem.java                (Inheritance & Simple String Matchable)
  |     +-- FoundItem.java               (Inheritance)
  |     +-- Storable.java                (Interface)
  |     +-- Matchable.java               (Interface)
  |     +-- MatchResult.java             (Comparable model)
  |     +-- InvalidItemException.java    (Custom exception)
  |     +-- ItemNotFoundException.java   (Custom exception)
  |     +-- FileManager.java             (File I/O operations)
  |     +-- ItemManager.java             (Business logic & Collections)
  |     +-- AutoMatchThread.java         (Multithreading & Synchronization)
  |     +-- Main.java                    (Console menu-driven runner)
  |     +-- MainFrame.java               (Simplified Swing GUI)
  +-- data/
  |     +-- lost_items.txt               (Saved lost records)
  |     +-- found_items.txt              (Saved found records)
  |     +-- match_report.txt             (Generated match export)
  +-- Campus_Lost_Found_Mini_Project_Report.docx (Word Document Report)
  +-- generate_report.py                 (Simplified report generator script)
  +-- run.bat                            (One-click build and run script)
  +-- README.txt                         (Project documentation)
==================================================================
