package bg.sofia.uni.fmi.mjt.project.models.database.relation;

import java.util.Objects;
import java.util.UUID;

public class Relation {
    private static final int ID_POS = 0;
    private static final int NAME_POS = 1;

    private final UUID id;
    private final String type;
    private final String name;

    public Relation(String name, String type) {
        this(UUID.randomUUID(), name, type);
    }

    public Relation(UUID id, String name, String type) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return '{' + id.toString() + ';' + name + ';';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return Objects.equals(id, relation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
