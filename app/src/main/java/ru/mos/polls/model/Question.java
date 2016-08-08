package ru.mos.polls.model;


public class Question {

    private final int id;
    private final String question;
    private final QuestType type;
    private final Variant[] variants;


    public Question(int id, String question, QuestType type, Variant[] variants) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.variants = variants;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public QuestType getType() {
        return type;
    }

    public Variant[] getVariants() {
        return variants;
    }
}

