package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.widget.ImageView;

import ru.mos.polls.newprofile.model.Achievement;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementVM extends BaseObservable {
    private Achievement achievement;
    private final Context context;

    public AchievementVM(Achievement achievement, Context context) {
        this.achievement = achievement;
        this.context = context;
    }

    public String getId() {
        return achievement.getId();
    }

    public String getImageUrl() {
        return achievement.getImageUrl();
    }

    public String getTitle() {
        return achievement.getTitle();
    }

    public String getDescription() {
        return achievement.getDescription();
    }

    public String getBody() {
        return achievement.getBody();
    }

    public boolean isNext() {
        return achievement.isNext();
    }

    public boolean isNeedHideTask() {
        return achievement.isNeedHideTask();
    }
}
