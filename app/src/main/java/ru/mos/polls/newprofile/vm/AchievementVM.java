package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.AGApplication;
import ru.mos.polls.databinding.ItemAchievementBinding;
import ru.mos.polls.newprofile.model.Achievement;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementVM extends BaseObservable {
    private Achievement achievement;
    private ItemAchievementBinding binding;
    ImageView badge;
    ProgressBar pb;

    public AchievementVM(Achievement achievement, ItemAchievementBinding binding) {
        this.achievement = achievement;
        this.binding = binding;
        badge = binding.badgeContainer.badge;
        pb = binding.badgeContainer.loadingBadge;
        loadImage(getImageUrl());
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


    public void loadImage(String url) {
        ImageLoader imageLoader = AGApplication.getImageLoader();
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    badge.setImageBitmap(bitmap);
                    pb.setVisibility(View.GONE);
                    badge.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

}
