package bg.sofia.uni.fmi.mjt.project.user.models;

import java.util.UUID;

public record UserModel(UUID id, String username, String password,
                        String firstName, String lastName, String currencyPreference) { }
