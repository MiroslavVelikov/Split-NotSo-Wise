package bg.sofia.uni.fmi.mjt.project.service;

import bg.sofia.uni.fmi.mjt.project.models.database.Notification;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.Relation;
import bg.sofia.uni.fmi.mjt.project.models.database.relation.RelationMember;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Split;
import bg.sofia.uni.fmi.mjt.project.models.database.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.project.models.database.user.User;
import bg.sofia.uni.fmi.mjt.project.models.output.NotificationManager;
import bg.sofia.uni.fmi.mjt.project.models.output.NotificationOutput;
import bg.sofia.uni.fmi.mjt.project.repositories.Storage;
import bg.sofia.uni.fmi.mjt.project.utils.HashUtils;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Service {
    private final Storage storage;

    public Service(Storage storage) {
        this.storage = storage;
    }

    public User getUserByUsername(String username) {
        return storage.getUserByUsername(username);
    }

    public User getUserById(UUID userId) {
        return storage.getUserById(userId);
    }

    public NotificationManager login(String username, String password) {
        User user = storage.getUserByUsername(username);
        if (!user.getPassword().equals(HashUtils.sha256(password))) {
            return null;
        }

        return getNotificationsForUser(user);
    }

    public User register(String username, String password, String firstName, String lastName) {
        User newUser = new User(username, HashUtils.sha256(password), firstName, lastName);
        storage.createUser(newUser);

        return newUser;
    }

    public Relation addFriend(UUID currentUserId, String friendUsername) {
        try {
            User friend = storage.getUserByUsername(friendUsername);
            if (friend == null) {
                throw new RuntimeException(String.format("User with this username (%s) does not exist", friendUsername));
            }

            Set<Relation> currentUserRelations = storage.getRelationsContainingUser(currentUserId);
            Set<Relation> friendsRelations = storage.getRelationsContainingUser(friend.getId());

            boolean areFriends = currentUserRelations.stream()
                .anyMatch(r -> r.getType().equals("friend") && friendsRelations.contains(r));
            if (areFriends) {
                throw new RuntimeException(String.format("Users (you, %s) are already friends", friendUsername));
            }

            Relation newRelation = new Relation("", "friend");
            storage.createRelation(newRelation);

            RelationMember currentUserRelation = new RelationMember(currentUserId, newRelation.getId());
            RelationMember friendRelation = new RelationMember(friend.getId(), newRelation.getId());
            storage.addUserToRelation(currentUserRelation);
            storage.addUserToRelation(friendRelation);

            return newRelation;
        } catch (RuntimeException _) {
            return null;
        }
    }

    public Relation createGroup(UUID currentUserId, String groupName, Set<String> usersUsernames) {
        try {
            Set<User> users = usersUsernames.stream()
                .map(storage::getUserByUsername)
                .collect(Collectors.toSet());

            Relation newRelation = new Relation(groupName, "group");
            storage.createRelation(newRelation);


            for (User user : users) {
                RelationMember newMember = new RelationMember(user.getId(), newRelation.getId());
                storage.addUserToRelation(newMember);
            }
            RelationMember newMember = new RelationMember(currentUserId, newRelation.getId());
            storage.addUserToRelation(newMember);

            return newRelation;
        } catch (RuntimeException _) {
            return null;
        }
    }

    public Split createSplit(UUID currentUserId, UUID relationId, Double amount, String reason) {
        try {
            Split newSplit = new Split(amount, reason, relationId);
            Set<RelationMember> usersInRelation = storage.getMembersOfRelationById(relationId).stream()
                .filter(ur -> !ur.getMemberId().equals(currentUserId))
                .collect(Collectors.toSet());

            if (usersInRelation.isEmpty()) {
                throw new RuntimeException("This relation is empty");
            }

            storage.createSplit(newSplit);
            for (RelationMember relationMember : usersInRelation) {
                Transaction newTransaction = new Transaction(newSplit.getId(), amount,
                    relationMember.getMemberId(), currentUserId);
                storage.createTransaction(newTransaction);
            }

            return newSplit;
        } catch (RuntimeException _) {
            return null;
        }
    }

    public Set<Relation> getMutualRelations(UUID user1, UUID user2) {
        Set<Relation> user1Relations = storage.getRelationsContainingUser(user1);
        Set<Relation> user2Relations = storage.getRelationsContainingUser(user2);

        Set<Relation> mutualRelations = new HashSet<>(user1Relations);
        mutualRelations.retainAll(user2Relations);

        return mutualRelations;
    }

//    public Set<Transaction> getDebtStatus(UUID user1, UUID user2) {
//        return storage.getTransactionsByUser(user1).stream()
//            .filter(t -> !t.isPaid()
//                && t.getReceiverId().equals(user1)
//                && t.getSenderId().equals(user2))
//            .collect(Collectors.toSet());
//    }

    public Set<Transaction> getUserPayedTransactions(UUID userId) {
        return storage.getTransactionsByUser(userId).stream()
            .filter(Transaction::isPaid)
            .collect(Collectors.toSet());
    }

    public Set<Transaction> getUserNotPayedTransactions(UUID userId) {
        return storage.getTransactionsByUser(userId).stream()
            .filter(t -> !t.isPaid())
            .collect(Collectors.toSet());
    }

    public Double payTransaction(UUID currentUserId, Double amount, UUID payerId) {
        Set<Transaction> debts = storage.getTransactionsByUser(currentUserId).stream()
            .filter(t -> !t.isPaid()
                && t.getSenderId().equals(payerId)
            ).collect(Collectors.toSet());

        Double debtToPay = 0.0;
        for (Transaction debt : debts) {
            if (debt.getAmount() > amount && amount != 0) {
                debt.setAmount(debt.getAmount() - amount);
                amount = 0.0;
                storage.updateTransaction(debt);
            } else if (amount != 0) {
                debt.setPaid(true);
                amount -= debt.getAmount();
                storage.updateTransaction(debt);

                continue;
            }

            debtToPay += debt.getAmount();
        }

        return debtToPay;
    }

    private NotificationManager getNotificationsForUser(User user) {
        Set<Notification> notifications = storage.getAllNotificationByUserId(user.getId());

        Map<String, Set<NotificationOutput>> notificationOutputs = notifications.stream()
            .filter(n -> getTransactionById(n.getTransactionId()).isPaid())
            .map(n -> {
                Transaction transaction = getTransactionById(n.getTransactionId());
                Split split = getSplitById(transaction.getSplitId());
                Relation relation = getRelationById(split.getRelationId());

                // FIX MESSAGE
                NotificationOutput notificationOutput = new NotificationOutput(split.getReason() + " " + user.getFirstName());
                String key = relation.getName().isEmpty() ? "friend" : relation.getName();

                return new AbstractMap.SimpleEntry<>(key, notificationOutput);
            })
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
            ));

        Set<NotificationOutput> friendsNotifications = notificationOutputs.get("friend");
        notificationOutputs.remove("friend");

        return new NotificationManager(user, friendsNotifications, notificationOutputs);
    }

    private Transaction getTransactionById(UUID transactionId) {
        return storage.getTransactionById(transactionId);
    }

    public Split getSplitById(UUID splitId) {
        return storage.getSplitById(splitId);
    }

    public Relation getRelationById(UUID relationId) {
        return storage.getRelationById(relationId);
    }
}
