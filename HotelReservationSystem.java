import java.io.*;
import java.util.*;

class Room {
    String category;
    int roomNumber;
    boolean isBooked;

    Room(String category, int roomNumber) {
        this.category = category;
        this.roomNumber = roomNumber;
        this.isBooked = false;
    }
}

class Booking implements Serializable {
    String customerName;
    String category;
    int roomNumber;
    String bookingID;

    Booking(String customerName, String category, int roomNumber) {
        this.customerName = customerName;
        this.category = category;
        this.roomNumber = roomNumber;
        this.bookingID = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "\nBooking ID: " + bookingID +
               "\nName: " + customerName +
               "\nCategory: " + category +
               "\nRoom No: " + roomNumber;
    }
}

class HotelSystem {
    Map<Integer, Room> rooms = new HashMap<>();
    List<Booking> bookings = new ArrayList<>();
    final String FILE = "bookings.ser";

    HotelSystem() {
        loadBookings();
        initRooms();
    }

    void initRooms() {
        for (int i = 1; i <= 10; i++) rooms.put(i, new Room("Standard", i));
        for (int i = 11; i <= 15; i++) rooms.put(i, new Room("Deluxe", i));
        for (int i = 16; i <= 18; i++) rooms.put(i, new Room("Suite", i));
        for (Booking b : bookings) {
            if (rooms.containsKey(b.roomNumber)) {
                rooms.get(b.roomNumber).isBooked = true;
            }
        }
    }

    void displayAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms.values()) {
            if (!r.isBooked) {
                System.out.println("Room No: " + r.roomNumber + " (" + r.category + ")");
            }
        }
    }

    void bookRoom(Scanner sc) {
        System.out.print("Enter Your Name: ");
        String name = sc.nextLine();
        displayAvailableRooms();
        System.out.print("Enter Room Number to Book: ");
        int roomNum = sc.nextInt(); sc.nextLine();

        if (rooms.containsKey(roomNum) && !rooms.get(roomNum).isBooked) {
            Room r = rooms.get(roomNum);
            Booking b = new Booking(name, r.category, roomNum);
            bookings.add(b);
            r.isBooked = true;
            saveBookings();
            simulatePayment(sc);
            System.out.println("✅ Booking Successful!");
            System.out.println(b);
        } else {
            System.out.println("❌ Invalid or Already Booked Room.");
        }
    }

    void cancelBooking(Scanner sc) {
        System.out.print("Enter Booking ID to Cancel: ");
        String id = sc.nextLine();
        Booking found = null;
        for (Booking b : bookings) {
            if (b.bookingID.equals(id)) {
                found = b;
                break;
            }
        }
        if (found != null) {
            rooms.get(found.roomNumber).isBooked = false;
            bookings.remove(found);
            saveBookings();
            System.out.println("✅ Booking Cancelled.");
        } else {
            System.out.println("❌ Booking ID not found.");
        }
    }

    void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("All Bookings:");
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    void simulatePayment(Scanner sc) {
        System.out.println("💳 Simulating Payment...");
        System.out.print("Enter card number: ");
        sc.nextLine(); // Skip card entry simulation
        System.out.println("Processing payment...");
        try { Thread.sleep(1000); } catch (Exception e) {}
        System.out.println("✅ Payment Successful!");
    }

    void saveBookings() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    void loadBookings() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {
            bookings = (List<Booking>) in.readObject();
        } catch (Exception e) {
            System.out.println("Error loading bookings.");
        }
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelSystem system = new HotelSystem();

        while (true) {
            System.out.println("\n=== HOTEL RESERVATION SYSTEM ===");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose Option: ");

            int choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1: system.displayAvailableRooms(); break;
                case 2: system.bookRoom(sc); break;
                case 3: system.cancelBooking(sc); break;
                case 4: system.viewBookings(); break;
                case 5: System.out.println("Thank you!"); return;
                default: System.out.println("❌ Invalid Choice.");
            }
        }
    }
}