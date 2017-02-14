package ru.mos.polls.tutorial;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.io.Serializable;

import ru.mos.polls.R;

public class Tutorial implements Serializable {
    public static final Tutorial[] DEFAULTS = new Tutorial[] {
            new Tutorial(R.drawable.im_1, R.string.tutorial_description_1),
            new Tutorial(R.drawable.im_2, R.string.tutorial_description_2),
            new Tutorial(R.drawable.im_3, R.string.tutorial_description_3)
    };

    @DrawableRes
    private int imageDrawableId;
    @StringRes
    private int descriptionDrawableId;

    Tutorial(@DrawableRes int imageDrawableId, @StringRes int descriptionDrawableId) {
        this.imageDrawableId = imageDrawableId;
        this.descriptionDrawableId = descriptionDrawableId;
    }

    public int getImageDrawableId() {
        return imageDrawableId;
    }

    public int getDescriptionDrawableId() {
        return descriptionDrawableId;
    }
}
