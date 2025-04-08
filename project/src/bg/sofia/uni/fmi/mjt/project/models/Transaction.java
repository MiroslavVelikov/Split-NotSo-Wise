package bg.sofia.uni.fmi.mjt.project.models;

import java.util.UUID;

public record Transaction(UUID connectionID, String reason, Double amount) { }
