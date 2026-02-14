package bg.sofia.uni.fmi.mjt.project.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static final String DEFAULT_USER_REPO_PATH = "./user.csv";

    public static final String QUIT_COMMAND = "quit";

    private static final String HASHING_ALGORITHM = "SHA-256";

    // This method is taken from https://medium.com/@AlexanderObregon/what-is-sha-256-hashing-in-java-0d46dfb83888
    public static String hashPassword(String input) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);

            // Perform the hash computation
            byte[] encodedhash = digest.digest(input.getBytes());

            // Convert byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
