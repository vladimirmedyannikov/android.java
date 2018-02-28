package ru.mos.polls.friend.model;

import java.util.List;

import ru.mos.polls.profile.model.Statistics;


public class Statistic {
    private String status;
    private int rating;
    private List<Statistics> params;

    public String getStatus() {
        return status;
    }

    public int getRating() {
        return rating;
    }

    public List<Statistics> getParams() {
        return params;
    }
}
