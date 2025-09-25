package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceUtil {

    private static final Logger log = Logger.getLogger(UserServiceUtil.class.getName());
    private static final int BCRYPT_ROUNDS = 12;

    // Private constructor to prevent instantiation
    private UserServiceUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error hashing password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            log.warning("Password or hash is null during verification");
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            log.log(Level.WARNING, "Error checking password", e);
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 6 &&
                password.length() <= 100;
    }
}