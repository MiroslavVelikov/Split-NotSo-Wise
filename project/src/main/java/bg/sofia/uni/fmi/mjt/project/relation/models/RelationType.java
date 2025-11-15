package bg.sofia.uni.fmi.mjt.project.relation.models;

public enum RelationType {

    FRIENDSHIP,
    GROUP;

    public static RelationType fromStringToRelationType(String str) {
        return switch (str){
            case "FRIENDSHIP" -> RelationType.FRIENDSHIP;
            default -> RelationType.GROUP;
        };
    }

}
