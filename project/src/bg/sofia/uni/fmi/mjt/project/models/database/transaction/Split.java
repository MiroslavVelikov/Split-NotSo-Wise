package bg.sofia.uni.fmi.mjt.project.models.database.transaction;

import java.util.Objects;
import java.util.UUID;

public class Split {
    private final UUID id;
    private final UUID relationId;
    private final Double amount;
    private final String reason;

    public Split(Double amount, String reason, UUID relationId) {
        this(UUID.randomUUID(), amount, reason, relationId);
    }

    public Split(UUID id, Double amount, String reason, UUID relationId) {
        this.id = id;
        this.amount = amount;
        this.reason = reason;
        this.relationId = relationId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRelationId() {
        return relationId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return '{' + id.toString() + ';' + relationId.toString() +
            ';' + amount + ';' + reason + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Split split = (Split) o;
        return Objects.equals(id, split.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
