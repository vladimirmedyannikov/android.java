package ru.mos.polls.friend.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.elk.profile.Achievements;


/**
 * Created by wlTrunks on 29.08.2017.
 */

public class AchievementsData {
    private int count;
    @SerializedName("last")
    private List<Achievements> last;

    public int getCount() {
        return count;
    }

    public List<Achievements> getLast() {
        return last;
    }
}
