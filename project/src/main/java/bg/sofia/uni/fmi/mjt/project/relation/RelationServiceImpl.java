package bg.sofia.uni.fmi.mjt.project.relation;

import bg.sofia.uni.fmi.mjt.project.relation.models.Relation;
import bg.sofia.uni.fmi.mjt.project.relation.models.RelationType;

import java.util.Set;
import java.util.UUID;

public class RelationServiceImpl implements RelationService {

    private static final String DEFAULT_FRIENDSHIP_NAME = "";
    private static final int MAX_FRIENDS_IN_FRIENDSHIP = 2;

    private final RelationRepository storage;

    public RelationServiceImpl(RelationRepository storage) {
        this.storage = storage;
    }

    @Override
    public Relation createRelation(String name, Set<String> users) {
        boolean isGroup = users.size() > MAX_FRIENDS_IN_FRIENDSHIP;
        Relation relation = new Relation(UUID.randomUUID(), isGroup ? name : DEFAULT_FRIENDSHIP_NAME,
            users, isGroup ? RelationType.GROUP : RelationType.FRIENDSHIP);

        return storage.addRelation(relation);
    }

    @Override
    public Relation getRelationById(UUID id) {
        return storage.getRelationById(id);
    }

    @Override
    public Set<Relation> getRelationsByUsername(String username) {
        return storage.getRelationsByMember(username);
    }
}
