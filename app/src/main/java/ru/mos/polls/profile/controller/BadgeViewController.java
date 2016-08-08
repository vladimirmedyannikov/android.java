package ru.mos.polls.profile.controller;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley2.VolleyError;
import com.android.volley2.toolbox.ImageLoader;

import ru.mos.polls.profile.model.Achievement;


public class BadgeViewController {
    public static void displayBadge(final ImageView badge, final ProgressBar loadingBadgeProgress, Achievement achievement, ImageLoader imageLoader) {
        if (achievement.getImageUrl() != null
                && !TextUtils.isEmpty(achievement.getImageUrl())
                && !"null".equalsIgnoreCase(achievement.getImageUrl())) {
            badge.setVisibility(View.GONE);
            loadingBadgeProgress.setVisibility(View.VISIBLE);
            imageLoader.get(achievement.getImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer != null) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            badge.setImageBitmap(bitmap);
                            badge.setVisibility(View.VISIBLE);
                            loadingBadgeProgress.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    badge.setVisibility(View.GONE);
                    loadingBadgeProgress.setVisibility(View.GONE);
                }

                private void setImageVisible(boolean visibility) {
                    badge.setVisibility(visibility ? View.VISIBLE : View.GONE);
                    loadingBadgeProgress.setVisibility(visibility ? View.GONE : View.VISIBLE);
                }
            });
        }
    }
}
