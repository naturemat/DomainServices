package domain.model.valueobjects;

public enum SportType {
    GYM("Gimnasio"),
    FOOTBALL("Fútbol");

    private final String displayName;

    SportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}