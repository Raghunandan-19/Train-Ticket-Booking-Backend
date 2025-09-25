package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserBookingService {
    private static final Logger log = Logger.getLogger(UserBookingService.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList;
    private User user;
    private static final String USER_FILE_PATH = "src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        Path filePath = Paths.get(USER_FILE_PATH);
        if (!Files.exists(filePath)) {
            // Create empty user list file if it doesn't exist
            Files.createDirectories(filePath.getParent());
            userList = new ArrayList<>();
            saveUserListToFile();
            return;
        }

        try {
            userList = objectMapper.readValue(filePath.toFile(), new TypeReference<List<User>>() {});
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to load users from file, starting with empty list", e);
            userList = new ArrayList<>();
        }
    }

    public boolean loginUser() {
        if (user == null) return false;

        return userList.stream()
                .anyMatch(existingUser ->
                        existingUser.getName().equals(user.getName()) &&
                                UserServiceUtil.checkPassword(user.getPassword(), existingUser.getHashedPassword())
                );
    }

    public boolean signUp(User newUser) {
        if (newUser == null || newUser.getName() == null || newUser.getPassword() == null) {
            return false;
        }

        // Check if user already exists
        boolean userExists = userList.stream()
                .anyMatch(existingUser -> existingUser.getName().equals(newUser.getName()));

        if (userExists) {
            System.out.println("User already exists!");
            return false;
        }

        try {
            userList.add(newUser);
            saveUserListToFile();
            return true;
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to save user", ex);
            return false;
        }
    }

    private void saveUserListToFile() throws IOException {
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }

    public void fetchBookings() {
        if (user == null) {
            System.out.println("Please login first!");
            return;
        }

        userList.stream()
                .filter(existingUser ->
                        existingUser.getName().equals(user.getName()) &&
                                UserServiceUtil.checkPassword(user.getPassword(), existingUser.getHashedPassword())
                )
                .findFirst()
                .ifPresentOrElse(
                        User::printTickets,
                        () -> System.out.println("User not found or invalid credentials!")
                );
    }

    public boolean cancelBooking(String ticketId) {
        if (user == null) {
            System.out.println("Please login first!");
            return false;
        }

        if (ticketId == null || ticketId.trim().isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return false;
        }

        Optional<User> userOptional = userList.stream()
                .filter(existingUser ->
                        existingUser.getName().equals(user.getName()) &&
                                UserServiceUtil.checkPassword(user.getPassword(), existingUser.getHashedPassword())
                )
                .findFirst();

        if (userOptional.isEmpty()) {
            System.out.println("User not found!");
            return false;
        }

        User foundUser = userOptional.get();
        boolean removed = foundUser.getTicketsBooked().removeIf(ticket ->
                ticket.getTicketId().equals(ticketId.trim())
        );

        if (removed) {
            try {
                saveUserListToFile();
                System.out.println("Ticket with ID " + ticketId + " has been cancelled.");
                return true;
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed to save after cancellation", e);
                return false;
            }
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return false;
        }
    }

    public List<Train> getTrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            log.log(Level.WARNING, "Failed to get trains", ex);
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train != null ? train.getSeats() : new ArrayList<>();
    }

    public boolean bookTrainSeat(Train train, int row, int seat) {
        if (train == null || train.getSeats() == null) {
            return false;
        }

        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() &&
                    seat >= 0 && seat < seats.get(row).size()) {

                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.updateTrain(train);
                    return true;
                } else {
                    System.out.println("Seat is already booked!");
                    return false;
                }
            } else {
                System.out.println("Invalid row or seat number!");
                return false;
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to book seat", ex);
            return false;
        }
    }
}