package ru.mos.polls.poll.model;

/**
 * Created by Trunks on 03.11.2017.
 */

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
