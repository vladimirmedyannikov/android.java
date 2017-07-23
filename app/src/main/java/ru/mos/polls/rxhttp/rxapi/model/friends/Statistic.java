package ru.mos.polls.rxhttp.rxapi.model.friends;

import java.util.List;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 11:59.
 */

public class Statistic {
    private String status;
    private int rating;
    private List<Param> params;

    public String getStatus() {
        return status;
    }

    public int getRating() {
        return rating;
    }

    public List<Param> getParams() {
        return params;
    }
}
