package ru.mos.polls.newprofile.vm;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.AGApplication;
import ru.mos.polls.databinding.ItemAchievementBinding;
import ru.mos.polls.newprofile.base.vm.BaseVM;
import ru.mos.polls.newprofile.model.Achievement;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementVM extends BaseVM<Achievement, ItemAchievementBinding> {
    ImageView badge;
    ProgressBar pb;

    public AchievementVM(Achievement achievement, ItemAchievementBinding binding) {
        super(achievement, binding);
        badge = viewDataBinding.badgeContainer.badge;
        pb = viewDataBinding.badgeContainer.loadingBadge;
        loadImage(getImageUrl());
    }

    public String getId() {
        return model.getId();
    }

    public String getImageUrl() {
        return model.getImageUrl();
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getDescription() {
        return model.getDescription();
    }

    public String getBody() {
        return model.getBody();
    }

    public boolean isNext() {
        return model.isNext();
    }

    public boolean isNeedHideTask() {
        return model.isNeedHideTask();
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
