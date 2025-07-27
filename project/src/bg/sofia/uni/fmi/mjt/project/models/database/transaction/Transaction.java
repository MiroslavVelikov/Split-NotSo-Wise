package bg.sofia.uni.fmi.mjt.project.models.database.transaction;

import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private static final int ID_POS = 0;
    private static final int AMOUNT_POS = 1;
    private static final int SPLIT_ID_POS = 2;
    private static final int FROM_POS = 3;
    private static final int TO_POS = 4;

    private final UUID id;
    private Double amount;
    private final UUID splitId;
    private final UUID senderId;
    private final UUID receiverId;
    private boolean isPaid;

    public Transaction(UUID splitId, Double amount, UUID senderId, UUID receiverId) {
        this(UUID.randomUUID(), splitId, amount, senderId, receiverId, false);
    }

    public Transaction(UUID id, UUID splitId, Double amount, UUID senderId, UUID receiverId, boolean isPaid) {
        this.id = id;
        this.splitId = splitId;
        this.amount = amount;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isPaid = isPaid;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSplitId() {
        return splitId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    // FIX
    @Override
    public String toString() {
        return '{' + id.toString() + ';' + amount + ';' + splitId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction payment = (Transaction) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
