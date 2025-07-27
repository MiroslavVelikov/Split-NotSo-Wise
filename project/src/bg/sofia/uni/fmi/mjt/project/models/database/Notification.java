package bg.sofia.uni.fmi.mjt.project.models.database;

import java.util.Objects;
import java.util.UUID;

public class Notification {
    private static final short ID_POS = 0;
    private static final short PAYMENT_ID_POS = 1;
    private static final short USERNAME_POS = 2;

    private final UUID id;
    private final UUID transactionId;
    private final UUID userId;

    public Notification(UUID paymentId, UUID userId) {
        this(UUID.randomUUID(), paymentId, userId);
    }

    public Notification(UUID id, UUID paymentId, UUID userId) {
        this.id = id;
        this.transactionId = paymentId;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return '{' + id.toString() + ';' + transactionId.toString() + ';' + userId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
