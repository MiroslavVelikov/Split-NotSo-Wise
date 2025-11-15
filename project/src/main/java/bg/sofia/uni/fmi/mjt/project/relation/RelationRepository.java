package bg.sofia.uni.fmi.mjt.project.relation;

import bg.sofia.uni.fmi.mjt.project.relation.models.Relation;

import java.util.Set;
import java.util.UUID;

public interface RelationRepository {

    Relation addRelation(Relation relation);
    Relation getRelationById(UUID id);
    Set<Relation> getRelationsByMember(String username);
    Relation getRelationByName(String name);

}
