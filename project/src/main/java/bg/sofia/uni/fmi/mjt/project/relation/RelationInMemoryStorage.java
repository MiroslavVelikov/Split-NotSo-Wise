package bg.sofia.uni.fmi.mjt.project.relation;

import bg.sofia.uni.fmi.mjt.project.relation.models.Relation;
import bg.sofia.uni.fmi.mjt.project.relation.models.RelationType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RelationInMemoryStorage implements RelationRepository {

    private final String filePath;
    private Set<Relation> relations;

    public RelationInMemoryStorage(String filePath) {
        relations = new HashSet<>();
        this.filePath = filePath;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                Relation relation = new Relation(
                    UUID.fromString(parts[0].trim()),
                    parts[1].trim(),
                    Arrays.stream(parts[2].trim().split(";")).collect(Collectors.toSet()),
                    RelationType.fromStringToRelationType(parts[3].trim())
                );

                relations.add(relation);
            }

        } catch (IOException e) {

        }
    }

    @Override
    public Relation addRelation(Relation relation) {
        relations.add(relation);
        return relation;
    }

    @Override
    public Relation getRelationById(UUID id) {
        return relations.stream()
            .filter(relation -> relation.id().equals(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Set<Relation> getRelationsByMember(String username) {
        return relations.stream()
            .filter(relation -> relation.users().contains(username))
            .collect(Collectors.toSet());
    }

    @Override
    public Relation getRelationByName(String name) {
        return null;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,username,password,firstName,lastName,currencyPreference\n");

            for (Relation relation : relations) {
                writer.write(String.format(
                    "%s,%s,%s,%s\n",
                    relation.id(),
                    relation.name(),
                    usernamesToString(relation),
                    relation.type()
                ));
            }

        } catch (IOException e) {
            // FIX
            e.printStackTrace();
        }
    }

    private String usernamesToString(Relation relation) {
        StringBuilder result = new StringBuilder();

        for (String username : relation.users()) {
            result.append(username).append(";");
        }

        return result.toString();
    }

}
