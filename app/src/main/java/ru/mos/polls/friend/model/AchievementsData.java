package ru.mos.polls.friend.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.profile.model.Achievements;


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
