package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserStorageImpl implements UserStorage {

    private final String filePath;
    private Map<String, UserEntity> users;

    public UserStorageImpl(String filePath) {
        users = new HashMap<>();
        this.filePath = filePath;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                UserEntity user = new UserEntity(
                    UUID.fromString(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim()
                );

                users.putIfAbsent(user.getUsername(), user);
            }

        } catch (IOException e) {
            // FIX
            e.printStackTrace();
        }
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        users.putIfAbsent(user.getUsername(), user);
        return user;
    }

    @Override
    public UserEntity getUserByID(UUID id) {
        UserEntity user = users.values().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);

        return user;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity user = users.getOrDefault(username, null);
        return user;
    }

    private UserEntity getUserModelByUsername(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        UserEntity entity = getUserModelByUsername(user.getUsername());
        entity.setCurrencyPreference(user.getCurrencyPreference());

        return entity;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,username,password,firstName,lastName,currencyPreference\n");

            for (UserEntity user : users.values()) {
                writer.write(String.format(
                    "%s,%s,%s,%s,%s,%s\n",
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getCurrencyPreference()
                ));
            }

        } catch (IOException e) {
            // FIX
            e.printStackTrace();
        }
    }

}
