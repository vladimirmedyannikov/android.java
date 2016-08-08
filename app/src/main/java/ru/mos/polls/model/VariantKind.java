package ru.mos.polls.model;


public enum VariantKind {

    TEXT("text"),
    INPUT("input"),
    INTERVAL("interval");

    private final String typeStr;

    VariantKind(String typeStr) {
        this.typeStr = typeStr;
    }

    @Override
    public String toString() {
        return typeStr;
    }

    public static VariantKind fromString(String typeStr) {
        for (VariantKind kind : VariantKind.values()) {
            if (kind.toString().equalsIgnoreCase(typeStr))
                return kind;
        }

        return TEXT;
    }
}
