package bg.sofia.uni.fmi.mjt.project.relation.models;

import java.util.Set;
import java.util.UUID;

public record Relation(UUID id, String name,
                       Set<String> users, RelationType type) { }
