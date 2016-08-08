package ru.mos.polls.model;


public enum InputType {

    TEXT("text"),
    NUMBER("number"),
    DATE("date"),
    INT("int");

    private final String typeStr;

    InputType(String typeStr) {
        this.typeStr = typeStr;
    }

    @Override
    public String toString() {
        return typeStr;
    }

    public static InputType fromString(String typeStr) {
        for (InputType type : InputType.values()) {
            if (type.toString().equalsIgnoreCase(typeStr))
                return type;
        }

        return TEXT;
    }
}
