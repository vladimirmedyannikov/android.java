package ru.mos.polls.model;


public class Poll {

    public static final int ACTIVE = 0;
    public static final int PASSED = 1;
    public static final int INTERRUPTED = 2;

    final int id;
    final String title;
    final int points;
    private int status;
    private String author;
    int questCount;
    Question[] questions;
    final String mark;

    public Poll(int id, String title, int points, int status, int questCount, String mark) {
        this.id = id;
        this.title = title;
        this.points = points;
        this.status = status;
        this.questCount = questCount;
        this.mark = mark;
    }

    public int getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuestCount() {
        return questCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMark() {
        return mark;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
