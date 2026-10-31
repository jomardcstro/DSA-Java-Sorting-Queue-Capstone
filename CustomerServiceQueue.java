import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Customer Service Queue Management System
 * ------------------------------------------------------------
 * Procedural, console-based program (no custom classes / no OOP).
 *
 * DATA MANAGEMENT
 *   Five parallel ArrayLists store the customer records. The value at
 *   index i of every list belongs to the SAME customer:
 *       ids, names, concerns, durations, statuses
 *
 * QUEUE
 *   A Queue<Integer> (LinkedList) holds the customer IDs of people who
 *   are waiting. IDs are stored (not indexes) because sorting moves
 *   records around, which would make stored indexes wrong.
 *   Workflow:  Register -> Enqueue -> Serve (dequeue) -> Served
 *
 * SORTING (all written by hand, no built-in sort methods)
 *   - Insertion Sort : records by customer name (alphabetical)
 *   - Selection Sort : records by service duration
 *   - Bubble Sort    : records by customer ID (restore registration order)
 *   - Selection Sort : the waiting queue itself by duration (shortest first)
 */
public class CustomerServiceQueue {

    // Parallel ArrayLists: the same index in each list is the same customer.
    static ArrayList<Integer> ids = new ArrayList<>();
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<String> concerns = new ArrayList<>();
    static ArrayList<Integer> durations = new ArrayList<>();   // estimated minutes
    static ArrayList<String> statuses = new ArrayList<>();     // Registered / Waiting / Served

    // The service queue (FIFO). Holds the IDs of waiting customers.
    static Queue<Integer> waitingQueue = new LinkedList<>();

    static int nextId = 1001;               // next ID to assign
    static Scanner sc = new Scanner(System.in);

    // ======================================================================
    // MAIN
    // ======================================================================
    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ", 0, 11);
            System.out.println();

            switch (choice) {
                case 1:  registerCustomer();      break;
                case 2:  enqueueCustomer();       break;
                case 3:  serveNextCustomer();     break;
                case 4:  peekNextCustomer();      break;
                case 5:  displayQueue();          break;
                case 6:  checkQueueEmpty();       break;
                case 7:  displayRecords();        break;
                case 8:  sortRecordsMenu();       break;
                case 9:  sortQueueByDuration();   break;
                case 10: searchCustomer();        break;
                case 11: removeCustomer();        break;
                case 0:  System.out.println("Thank you for using the system. Goodbye!"); break;
            }
        } while (choice != 0);

        sc.close();
    }

    // Prints the main menu.
    static void showMenu() {
        System.out.println("\n==========================================");
        System.out.println("  CUSTOMER SERVICE QUEUE MANAGEMENT SYSTEM");
        System.out.println("==========================================");
        System.out.println(" 1  - Register a customer");
        System.out.println(" 2  - Add customer to service queue (enqueue)");
        System.out.println(" 3  - Serve next customer (dequeue)");
        System.out.println(" 4  - Peek at next customer");
        System.out.println(" 5  - Display waiting queue");
        System.out.println(" 6  - Check if queue is empty");
        System.out.println(" 7  - Display all customer records");
        System.out.println(" 8  - Sort customer records");
        System.out.println(" 9  - Sort waiting queue by service duration");
        System.out.println(" 10 - Search customer");
        System.out.println(" 11 - Remove customer record");
        System.out.println(" 0  - Exit");
        System.out.println("------------------------------------------");
    }

    // ======================================================================
    // RECORD MANAGEMENT
    // ======================================================================

    // Adds one new record to ALL parallel lists so they stay synchronized.
    static void registerCustomer() {
        System.out.println("--- Register Customer ---");
        String name = readNonEmpty("Customer name: ");
        String concern = readNonEmpty("Concern / service needed: ");
        int duration = readInt("Estimated service duration (minutes, 1-180): ", 1, 180);

        int id = nextId++;
        ids.add(id);
        names.add(name);
        concerns.add(concern);
        durations.add(duration);
        statuses.add("Registered");

        System.out.println("Customer registered successfully! Assigned ID: " + id);
    }

    // Displays every stored record in a table.
    static void displayRecords() {
        System.out.println("--- All Customer Records ---");
        if (ids.isEmpty()) {
            System.out.println("No customer records found.");
            return;
        }
        printTableHeader();
        for (int i = 0; i < ids.size(); i++) {
            printRecordRow(i);
        }
    }

    // Prints the column headings for the records table.
    static void printTableHeader() {
        System.out.printf("%-6s %-20s %-25s %-10s %-10s%n",
                "ID", "Name", "Concern", "Duration", "Status");
        System.out.println("---------------------------------------------------------------------------");
    }

    // Prints one record (all five lists at index i) as a table row.
    static void printRecordRow(int i) {
        System.out.printf("%-6d %-20s %-25s %-10s %-10s%n",
                ids.get(i), names.get(i), concerns.get(i),
                durations.get(i) + " min", statuses.get(i));
    }

    // Linear search: returns the index of the given ID, or -1 if not found.
    static int findIndexById(int id) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                return i;
            }
        }
        return -1;
    }

    // Removes a record from every list (and from the queue if waiting).
    static void removeCustomer() {
        System.out.println("--- Remove Customer Record ---");
        if (ids.isEmpty()) {
            System.out.println("No customer records to remove.");
            return;
        }
        int id = readInt("Enter customer ID to remove: ", 1, Integer.MAX_VALUE);
        int idx = findIndexById(id);
        if (idx == -1) {
            System.out.println("Customer ID " + id + " was not found.");
            return;
        }

        // If the customer is still waiting, take them out of the queue too.
        if (statuses.get(idx).equals("Waiting")) {
            waitingQueue.remove(Integer.valueOf(id));
        }

        // Remove the same index from ALL parallel lists.
        ids.remove(idx);
        names.remove(idx);
        concerns.remove(idx);
        durations.remove(idx);
        statuses.remove(idx);

        System.out.println("Customer record removed successfully.");
    }

    // ======================================================================
    // QUEUE OPERATIONS
    // ======================================================================

    // ENQUEUE: moves a registered customer into the waiting queue.
    static void enqueueCustomer() {
        System.out.println("--- Add Customer to Queue ---");
        if (ids.isEmpty()) {
            System.out.println("No registered customers yet. Please register a customer first.");
            return;
        }
        int id = readInt("Enter customer ID: ", 1, Integer.MAX_VALUE);
        int idx = findIndexById(id);
        if (idx == -1) {
            System.out.println("Customer ID " + id + " was not found.");
            return;
        }
        String status = statuses.get(idx);
        if (status.equals("Waiting")) {
            System.out.println(names.get(idx) + " is already in the queue.");
            return;
        }
        if (status.equals("Served")) {
            System.out.println(names.get(idx) + " has already been served.");
            return;
        }

        waitingQueue.add(id);            // enqueue at the rear
        statuses.set(idx, "Waiting");
        System.out.println(names.get(idx) + " added to the queue. Position: " + waitingQueue.size());
    }

    // DEQUEUE: serves the customer at the front of the queue (FIFO).
    static void serveNextCustomer() {
        System.out.println("--- Serve Next Customer ---");
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is empty. No customer to serve.");
            return;
        }
        int id = waitingQueue.poll();    // dequeue from the front
        int idx = findIndexById(id);
        statuses.set(idx, "Served");

        System.out.println("Now serving: " + names.get(idx) + " (ID " + id + ")");
        System.out.println("Concern    : " + concerns.get(idx));
        System.out.println("Est. time  : " + durations.get(idx) + " minutes");
        System.out.println("Customers still waiting: " + waitingQueue.size());
    }

    // PEEK: shows the front customer without removing them.
    static void peekNextCustomer() {
        System.out.println("--- Next Customer in Line ---");
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is empty. Nobody is waiting.");
            return;
        }
        int id = waitingQueue.peek();
        int idx = findIndexById(id);
        System.out.println("Next: " + names.get(idx) + " (ID " + id + ")");
        System.out.println("Concern: " + concerns.get(idx));
        System.out.println("Est. time: " + durations.get(idx) + " minutes");
    }

    // DISPLAY: prints the queue from front to rear without changing it.
    static void displayQueue() {
        System.out.println("--- Current Waiting Queue (front to rear) ---");
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        System.out.printf("%-4s %-6s %-20s %-25s %-10s%n",
                "No.", "ID", "Name", "Concern", "Duration");
        System.out.println("---------------------------------------------------------------------");
        int position = 1;
        int totalMinutes = 0;
        for (int id : waitingQueue) {    // iterating does NOT remove items
            int idx = findIndexById(id);
            System.out.printf("%-4d %-6d %-20s %-25s %-10s%n",
                    position, id, names.get(idx), concerns.get(idx),
                    durations.get(idx) + " min");
            totalMinutes += durations.get(idx);
            position++;
        }
        System.out.println("Total customers waiting: " + waitingQueue.size());
        System.out.println("Estimated total wait time: " + totalMinutes + " minutes");
    }

    // Reports whether the queue is empty.
    static void checkQueueEmpty() {
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is EMPTY.");
        } else {
            System.out.println("The queue is NOT empty. " + waitingQueue.size() + " customer(s) waiting.");
        }
    }

    // ======================================================================
    // SORTING: RECORDS
    // ======================================================================

    // Lets the user pick a sort, shows records before and after sorting.
    static void sortRecordsMenu() {
        System.out.println("--- Sort Customer Records ---");
        if (ids.size() < 2) {
            System.out.println("Need at least two records to sort.");
            return;
        }
        System.out.println("1 - By name, A to Z       (Insertion Sort)");
        System.out.println("2 - By name, Z to A       (Insertion Sort)");
        System.out.println("3 - By duration, shortest first (Selection Sort)");
        System.out.println("4 - By duration, longest first  (Selection Sort)");
        System.out.println("5 - By ID / registration order  (Bubble Sort)");
        System.out.println("0 - Cancel");
        int choice = readInt("Choose sort option: ", 0, 5);
        if (choice == 0) {
            System.out.println("Sorting cancelled.");
            return;
        }

        System.out.println("\nBEFORE sorting:");
        displayRecords();

        switch (choice) {
            case 1: insertionSortByName(true);      break;
            case 2: insertionSortByName(false);     break;
            case 3: selectionSortByDuration(true);  break;
            case 4: selectionSortByDuration(false); break;
            case 5: bubbleSortById();               break;
        }

        System.out.println("\nAFTER sorting:");
        displayRecords();
    }

    /*
     * Insertion Sort on customer names.
     * Suitable here because customer lists are small and often partly sorted,
     * and insertion sort is simple and stable (equal names keep their order).
     */
    static void insertionSortByName(boolean ascending) {
        for (int i = 1; i < names.size(); i++) {
            int j = i;
            // Move the record at j left until it is in the right place.
            while (j > 0 && shouldSwapNames(j - 1, j, ascending)) {
                swapRecords(j - 1, j);
                j--;
            }
        }
    }

    // Decides whether two neighboring records are out of order by name.
    static boolean shouldSwapNames(int left, int right, boolean ascending) {
        int cmp = names.get(left).compareToIgnoreCase(names.get(right));
        return ascending ? cmp > 0 : cmp < 0;
    }

    /*
     * Selection Sort on service duration.
     * Suitable because it makes very few swaps (at most n-1), and moving a
     * record means touching five parallel lists, so fewer swaps is useful.
     */
    static void selectionSortByDuration(boolean shortestFirst) {
        for (int i = 0; i < durations.size() - 1; i++) {
            int best = i;                          // index of smallest (or largest) so far
            for (int j = i + 1; j < durations.size(); j++) {
                boolean better = shortestFirst
                        ? durations.get(j) < durations.get(best)
                        : durations.get(j) > durations.get(best);
                if (better) {
                    best = j;
                }
            }
            if (best != i) {
                swapRecords(i, best);
            }
        }
    }

    /*
     * Bubble Sort on customer ID.
     * Used to restore the original registration order. Simple to trace and
     * explain, and fast enough for a small number of records.
     */
    static void bubbleSortById() {
        for (int pass = 0; pass < ids.size() - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < ids.size() - 1 - pass; j++) {
                if (ids.get(j) > ids.get(j + 1)) {
                    swapRecords(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;   // already sorted, stop early
            }
        }
    }

    // Swaps two records across ALL parallel lists so they stay in sync.
    static void swapRecords(int a, int b) {
        int tempId = ids.get(a);
        ids.set(a, ids.get(b));
        ids.set(b, tempId);

        String tempName = names.get(a);
        names.set(a, names.get(b));
        names.set(b, tempName);

        String tempConcern = concerns.get(a);
        concerns.set(a, concerns.get(b));
        concerns.set(b, tempConcern);

        int tempDuration = durations.get(a);
        durations.set(a, durations.get(b));
        durations.set(b, tempDuration);

        String tempStatus = statuses.get(a);
        statuses.set(a, statuses.get(b));
        statuses.set(b, tempStatus);
    }

    // ======================================================================
    // SORTING: THE QUEUE ITSELF
    // ======================================================================

    /*
     * Reorders the waiting queue so the shortest jobs are served first.
     * Steps: copy queue IDs into a list -> selection sort by duration ->
     * clear the queue -> enqueue the IDs back in the new order.
     * Note: this deliberately overrides pure first-come-first-served order.
     */
    static void sortQueueByDuration() {
        System.out.println("--- Sort Waiting Queue by Duration (shortest first) ---");
        if (waitingQueue.size() < 2) {
            System.out.println("Need at least two waiting customers to sort the queue.");
            return;
        }

        System.out.println("BEFORE sorting:");
        displayQueue();

        // Copy the queue into a temporary list (queue order preserved).
        ArrayList<Integer> temp = new ArrayList<>(waitingQueue);

        // Selection sort the temporary list using each customer's duration.
        for (int i = 0; i < temp.size() - 1; i++) {
            int best = i;
            for (int j = i + 1; j < temp.size(); j++) {
                int durJ = durations.get(findIndexById(temp.get(j)));
                int durBest = durations.get(findIndexById(temp.get(best)));
                if (durJ < durBest) {
                    best = j;
                }
            }
            if (best != i) {
                int swap = temp.get(i);
                temp.set(i, temp.get(best));
                temp.set(best, swap);
            }
        }

        // Rebuild the queue in sorted order.
        waitingQueue.clear();
        for (int id : temp) {
            waitingQueue.add(id);
        }

        System.out.println("\nAFTER sorting:");
        displayQueue();
    }

    // ======================================================================
    // SEARCH
    // ======================================================================

    // Searches records by exact ID or by full/partial name.
    static void searchCustomer() {
        System.out.println("--- Search Customer ---");
        if (ids.isEmpty()) {
            System.out.println("No customer records to search.");
            return;
        }
        System.out.println("1 - Search by ID");
        System.out.println("2 - Search by name (partial match allowed)");
        System.out.println("0 - Cancel");
        int choice = readInt("Choose search type: ", 0, 2);

        if (choice == 1) {
            int id = readInt("Enter customer ID: ", 1, Integer.MAX_VALUE);
            int idx = findIndexById(id);
            if (idx == -1) {
                System.out.println("No customer found with ID " + id + ".");
            } else {
                printTableHeader();
                printRecordRow(idx);
            }
        } else if (choice == 2) {
            String keyword = readNonEmpty("Enter name or part of the name: ").toLowerCase();
            boolean found = false;
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).toLowerCase().contains(keyword)) {
                    if (!found) {
                        printTableHeader();    // print the header only once
                        found = true;
                    }
                    printRecordRow(i);
                }
            }
            if (!found) {
                System.out.println("No customer found matching \"" + keyword + "\".");
            }
        } else {
            System.out.println("Search cancelled.");
        }
    }

    // ======================================================================
    // INPUT VALIDATION HELPERS
    // ======================================================================

    // Keeps asking until the user enters a whole number within [min, max].
    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Invalid input. Please enter a number from " + min
                        + (max == Integer.MAX_VALUE ? " and up." : " to " + max + "."));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Keeps asking until the user enters non-blank text.
    static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
}