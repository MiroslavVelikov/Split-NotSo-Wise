package bg.sofia.uni.fmi.mjt.project.models.database.relation;

import java.util.Objects;
import java.util.UUID;

public class RelationMember {
    private static final int ID_POS = 0;
    private static final int USER_ID_POS = 1;
    private static final int RELATION_ID_POS = 2;

    private final UUID relationId;
    private final UUID memberId;

    public RelationMember(UUID memberId, UUID relationId) {
        this.memberId = memberId;
        this.relationId = relationId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getRelationId() {
        return relationId;
    }

    @Override
    public String toString() {
        return '{' + memberId.toString() + ';' + relationId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RelationMember that = (RelationMember) o;
        return Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memberId);
    }
}
