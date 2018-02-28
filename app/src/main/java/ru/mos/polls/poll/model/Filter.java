package ru.mos.polls.poll.model;

public enum Filter {
    AVAILABLE("available"),
    PASSED("passed"),
    OLD("old");

    private String filter;

    Filter(String filter) {
        this.filter = filter;
    }


    @Override
    public String toString() {
        return filter;
    }
}
