package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");

        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.err.println("Failed to initialize booking service: " + ex.getMessage());
            return;
        }

        int option = 0;
        Train trainSelectedForBooking = new Train();

        while (option != 7) {
            displayMenu();
            option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1 -> handleSignUp(userBookingService);
                case 2 -> userBookingService = handleLogin();
                case 3 -> handleFetchBookings(userBookingService);
                case 4 -> trainSelectedForBooking = handleSearchTrains(userBookingService);
                case 5 -> handleBookSeat(userBookingService, trainSelectedForBooking);
                case 6 -> handleCancelBooking(userBookingService);
                case 7 -> System.out.println("Thank you for using Train Booking System!");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n=== Train Booking System ===");
        System.out.println("1. Sign up");
        System.out.println("2. Login");
        System.out.println("3. Fetch Bookings");
        System.out.println("4. Search Trains");
        System.out.println("5. Book a Seat");
        System.out.println("6. Cancel my Booking");
        System.out.println("7. Exit the App");
        System.out.print("Choose an option: ");
    }

    private static void handleSignUp(UserBookingService userBookingService) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = User.builder()
                .name(username)
                .password(password)
                .hashedPassword(UserServiceUtil.hashPassword(password))
                .ticketsBooked(new ArrayList<>())
                .userId(UUID.randomUUID().toString())
                .build();

        boolean success = userBookingService.signUp(user);
        System.out.println(success ? "Sign up successful!" : "Sign up failed!");
    }

    private static UserBookingService handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = User.builder()
                .name(username)
                .password(password)
                .hashedPassword(UserServiceUtil.hashPassword(password))
                .ticketsBooked(new ArrayList<>())
                .userId(UUID.randomUUID().toString())
                .build();

        try {
            UserBookingService service = new UserBookingService(user);
            System.out.println("Login successful!");
            return service;
        } catch (IOException ex) {
            System.err.println("Login failed: " + ex.getMessage());
            return null;
        }
    }

    private static void handleFetchBookings(UserBookingService userBookingService) {
        if (userBookingService == null) {
            System.out.println("Please login first!");
            return;
        }
        System.out.println("Fetching your bookings...");
        userBookingService.fetchBookings();
    }

    private static Train handleSearchTrains(UserBookingService userBookingService) {
        System.out.print("Enter source station: ");
        String source = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destination = scanner.nextLine();

        List<Train> trains = userBookingService.getTrains(source, destination);

        if (trains.isEmpty()) {
            System.out.println("No trains found for the given route.");
            return new Train();
        }

        AtomicInteger index = new AtomicInteger(1);
        trains.forEach(train -> {
            System.out.printf("%d. Train ID: %s%n", index.getAndIncrement(), train.getTrainId());
            train.getStationTimes().entrySet().forEach(entry ->
                    System.out.printf("   Station: %s, Time: %s%n", entry.getKey(), entry.getValue())
            );
        });

        System.out.print("Select a train (1, 2, 3...): ");
        int trainIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (trainIndex >= 0 && trainIndex < trains.size()) {
            return trains.get(trainIndex);
        } else {
            System.out.println("Invalid selection.");
            return new Train();
        }
    }

    private static void handleBookSeat(UserBookingService userBookingService, Train train) {
        if (userBookingService == null) {
            System.out.println("Please login first!");
            return;
        }

        if (train.getTrainId() == null) {
            System.out.println("Please search and select a train first!");
            return;
        }

        System.out.println("Available seats (0 = available, 1 = booked):");
        List<List<Integer>> seats = userBookingService.fetchSeats(train);

        for (int i = 0; i < seats.size(); i++) {
            System.out.printf("Row %d: ", i);
            seats.get(i).forEach(seat -> System.out.print(seat + " "));
            System.out.println();
        }

        System.out.print("Enter row number: ");
        int row = scanner.nextInt();
        System.out.print("Enter seat number: ");
        int seat = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Booking your seat...");
        boolean booked = userBookingService.bookTrainSeat(train, row, seat);
        System.out.println(booked ? "Booked! Enjoy your journey!" : "Can't book this seat.");
    }

    private static void handleCancelBooking(UserBookingService userBookingService) {
        if (userBookingService == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter ticket ID to cancel: ");
        String ticketId = scanner.nextLine();
        boolean cancelled = userBookingService.cancelBooking(ticketId);
        System.out.println(cancelled ? "Booking cancelled successfully!" : "Failed to cancel booking.");
    }
}