package ru.mos.polls.model;


public enum QuestType {

    RADIOBOX("radiobox"),
    CHECKBOX("checkbox"),
    RANKING("ranking");

    private final String typeStr;

    QuestType(String typeStr) {
        this.typeStr = typeStr;
    }

    @Override
    public String toString() {
        return typeStr;
    }

    public static QuestType fromString(String typeStr) {
        for (QuestType type : QuestType.values()) {
            if (type.toString().equalsIgnoreCase(typeStr))
                return type;
        }

        return RADIOBOX;
    }
}
