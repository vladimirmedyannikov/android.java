package ru.mos.polls.profile.controller;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.profile.model.Achievement;


public class BadgeViewController {
    public static void displayBadge(final ImageView badge, final ProgressBar loadingBadgeProgress, Achievement achievement, ImageLoader imageLoader) {
        if (achievement.getImageUrl() != null
                && !TextUtils.isEmpty(achievement.getImageUrl())
                && !"null".equalsIgnoreCase(achievement.getImageUrl())) {
            badge.setVisibility(View.GONE);
            loadingBadgeProgress.setVisibility(View.VISIBLE);
            imageLoader.loadImage(achievement.getImageUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    badge.setVisibility(View.GONE);
                    loadingBadgeProgress.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        badge.setImageBitmap(bitmap);
                        badge.setVisibility(View.VISIBLE);
                        loadingBadgeProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
    }
}
