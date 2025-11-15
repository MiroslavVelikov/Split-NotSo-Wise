package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserModel;
import bg.sofia.uni.fmi.mjt.project.user.models.UserService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserInMemoryStorage implements Storage {

    private final String filePath;
    private Map<String, UserModel> users;

    public UserInMemoryStorage(String filePath) {
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

                UserModel user = new UserModel(
                    UUID.fromString(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim()
                );

                users.putIfAbsent(user.username(), user);
            }

        } catch (IOException e) {
            // FIX
            e.printStackTrace();
        }
    }

    @Override
    public UserService addUser(UserModel user) {
        users.putIfAbsent(user.username(), user);
        return Convertor.convertUserModelToService(user);
    }

    @Override
    public UserService getUserByID(UUID id) {
        UserModel user = users.values().stream()
            .filter(u -> u.id().equals(id))
            .findFirst()
            .orElse(null);

        return Convertor.convertUserModelToService(user);
    }

    @Override
    public UserService getUserByUsername(String username) {
        UserModel user = users.getOrDefault(username, null);

        return Convertor.convertUserModelToService(user);
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,username,password,firstName,lastName,currencyPreference\n");

            for (UserModel user : users.values()) {
                writer.write(String.format(
                    "%s,%s,%s,%s,%s,%s\n",
                    user.id(),
                    user.username(),
                    user.password(),
                    user.firstName(),
                    user.lastName(),
                    user.currencyPreference()
                ));
            }

        } catch (IOException e) {
            // FIX
            e.printStackTrace();
        }
    }

}
