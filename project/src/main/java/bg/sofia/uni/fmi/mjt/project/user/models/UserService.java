package bg.sofia.uni.fmi.mjt.project.user.models;

import java.util.UUID;

public record UserService(UUID id, String username, String fullName, String currencyPreference) { }
