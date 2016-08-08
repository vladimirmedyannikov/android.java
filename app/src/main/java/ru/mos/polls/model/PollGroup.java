package ru.mos.polls.model;


public class PollGroup {

    private final int id;
    private String title;
    private Poll[] polls;

    public PollGroup(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public Poll[] getPolls() {
        return polls;
    }

    public void setPolls(Poll[] polls) {
        this.polls = polls;
    }

    public int getId() {
        return id;
    }

    public boolean isStored() {
        return id != -1;
    }

    public String getTitle() {
        return title;
    }
}
