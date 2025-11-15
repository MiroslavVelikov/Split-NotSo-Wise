package bg.sofia.uni.fmi.mjt.project.relation;

import bg.sofia.uni.fmi.mjt.project.relation.models.Relation;

import java.util.Set;
import java.util.UUID;

public interface Service {

    Relation createRelation(String name, Set<String> users);
    Relation getRelationById(UUID id);
    Set<Relation> getRelationsByUsername(String username);

}
