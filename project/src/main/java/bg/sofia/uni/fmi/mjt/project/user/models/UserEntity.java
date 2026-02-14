package bg.sofia.uni.fmi.mjt.project.user.models;

import java.util.UUID;

public class UserEntity {

    private final UUID id;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private String currencyPreference;

    public UserEntity(UUID id,
                     String username,
                     String password,
                     String firstName,
                     String lastName,
                     String currencyPreference) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currencyPreference = currencyPreference;
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

    public String getCurrencyPreference() {
        return currencyPreference;
    }

    public void setCurrencyPreference(String currencyPreference) {
        this.currencyPreference = currencyPreference;
    }

    public String toString() {
        return "UserModel{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", currencyPreference='" + currencyPreference + '\'' +
            '}';
    }
}