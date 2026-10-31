import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CustomerServiceQueue {
    static ArrayList<Integer> ids = new ArrayList<>();
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<String> concerns = new ArrayList<>();
    static ArrayList<Integer> durations = new ArrayList<>();
    static ArrayList<String> statuses = new ArrayList<>();

    static Queue<Integer> waitingQueue = new LinkedList<>();

    static int nextId = 1001;
    static Scanner sc = new Scanner(System.in);

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

    static void printTableHeader() {
        System.out.printf("%-6s %-20s %-25s %-10s %-10s%n",
                "ID", "Name", "Concern", "Duration", "Status");
        System.out.println("---------------------------------------------------------------------------");
    }

    static void printRecordRow(int i) {
        System.out.printf("%-6d %-20s %-25s %-10s %-10s%n",
                ids.get(i), names.get(i), concerns.get(i),
                durations.get(i) + " min", statuses.get(i));
    }

    static int findIndexById(int id) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                return i;
            }
        }
        return -1;
    }

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

        if (statuses.get(idx).equals("Waiting")) {
            waitingQueue.remove(Integer.valueOf(id));
        }

        ids.remove(idx);
        names.remove(idx);
        concerns.remove(idx);
        durations.remove(idx);
        statuses.remove(idx);

        System.out.println("Customer record removed successfully.");
    }

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

        waitingQueue.add(id);
        statuses.set(idx, "Waiting");
        System.out.println(names.get(idx) + " added to the queue. Position: " + waitingQueue.size());
    }

    static void serveNextCustomer() {
        System.out.println("--- Serve Next Customer ---");
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is empty. No customer to serve.");
            return;
        }
        int id = waitingQueue.poll();
        int idx = findIndexById(id);
        statuses.set(idx, "Served");

        System.out.println("Now serving: " + names.get(idx) + " (ID " + id + ")");
        System.out.println("Concern    : " + concerns.get(idx));
        System.out.println("Est. time  : " + durations.get(idx) + " minutes");
        System.out.println("Customers still waiting: " + waitingQueue.size());
    }

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
        for (int id : waitingQueue) {
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

    static void checkQueueEmpty() {
        if (waitingQueue.isEmpty()) {
            System.out.println("The queue is EMPTY.");
        } else {
            System.out.println("The queue is NOT empty. " + waitingQueue.size() + " customer(s) waiting.");
        }
    }

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

    static void insertionSortByName(boolean ascending) {
        for (int i = 1; i < names.size(); i++) {
            int j = i;

            while (j > 0 && shouldSwapNames(j - 1, j, ascending)) {
                swapRecords(j - 1, j);
                j--;
            }
        }
    }

    static boolean shouldSwapNames(int left, int right, boolean ascending) {
        int cmp = names.get(left).compareToIgnoreCase(names.get(right));
        return ascending ? cmp > 0 : cmp < 0;
    }

    static void selectionSortByDuration(boolean shortestFirst) {
        for (int i = 0; i < durations.size() - 1; i++) {
            int best = i;
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
                break;
            }
        }
    }

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

    static void sortQueueByDuration() {
        System.out.println("--- Sort Waiting Queue by Duration (shortest first) ---");
        if (waitingQueue.size() < 2) {
            System.out.println("Need at least two waiting customers to sort the queue.");
            return;
        }

        System.out.println("BEFORE sorting:");
        displayQueue();

        ArrayList<Integer> temp = new ArrayList<>(waitingQueue);

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

        waitingQueue.clear();
        for (int id : temp) {
            waitingQueue.add(id);
        }

        System.out.println("\nAFTER sorting:");
        displayQueue();
    }

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
                        printTableHeader();
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