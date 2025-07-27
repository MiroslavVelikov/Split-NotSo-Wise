package bg.sofia.uni.fmi.mjt.project.repositories;

import bg.sofia.uni.fmi.mjt.project.models.database.Notification;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.Relation;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.RelationMember;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Split;
import bg.sofia.uni.fmi.mjt.project.models.database.user.Currency;
import bg.sofia.uni.fmi.mjt.project.models.database.user.User;

import java.util.Set;
import java.util.UUID;

public interface Storage {
    void createUser(User newUser);

    User getUserById(UUID userId);

    User getUserByUsername(String username);

    void updateUserCurrencyType(User user, Currency currency);

    void createRelation(Relation newRelation);

    Relation getRelationById(UUID relationId);

    void addUserToRelation(RelationMember newMember);

    Set<Relation> getRelationsContainingUser(UUID userId);

    Set<RelationMember> getMembersOfRelationById(UUID relationId);

    void createSplit(Split newSplit);

    Split getSplitById(UUID splitId);

    void createTransaction(Transaction newTransaction);

    Transaction getTransactionById(UUID transactionId);

    Set<Transaction> getTransactionsByUser(UUID userId);

    void updateTransaction(Transaction newTransaction);

    void createNotification(Notification newNotification);

    Set<Notification> getAllNotificationByUserId(UUID userId);

    void deleteNotification(UUID notificationsId);
}
