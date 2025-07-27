package bg.sofia.uni.fmi.mjt.project.models.output;

import bg.sofia.uni.fmi.mjt.project.models.database.user.User;

import java.util.Map;
import java.util.Set;

public record NotificationManager(User user, Set<NotificationOutput> friendsNotifications,
                                  Map<String, Set<NotificationOutput>> groupsNotifications) { }
