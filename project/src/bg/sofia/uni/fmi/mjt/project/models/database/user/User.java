package bg.sofia.uni.fmi.mjt.project.models.database.user;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class User {
    private static final String ALGORITHM = "SHA-256";
    private static final short USERNAME_POS = 0;
    private static final short PASSWORD_POS = 1;
    private static final short FIRST_NAME_POS = 2;
    private static final short LAST_NAME_POS = 3;
    private static final short CURRENCY_PREFERENCE_POS = 4;

    private final UUID id;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private Currency currencyPreference;

    public User(String username, String password, String firstName, String lastName) {
        this(UUID.randomUUID(), username, password, firstName, lastName, Currency.BGN);
    }

    public User(UUID id, String username, String password, String firstName, String lastName, Currency currency) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currencyPreference = currency;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Currency getCurrencyPreference() {
        return currencyPreference;
    }

    public void setCurrencyPreference(Currency currencyPreference) {
        this.currencyPreference = currencyPreference;
    }

    @Override
    public String toString() {
        return '{' + username + ';' + password + ';' +
            firstName + ';' + lastName + ';' +
            currencyPreference + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
