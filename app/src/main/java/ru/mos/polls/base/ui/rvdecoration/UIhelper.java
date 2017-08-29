package ru.mos.polls.base.ui.rvdecoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;

/**
 * Created by Trunks on 11.08.2017.
 */

public class UIhelper {

    public static void setRecyclerList(RecyclerView recyclerView, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setNestedScrollingEnabled(false);
        Drawable dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider);
        android.support.v7.widget.DividerItemDecoration did = new android.support.v7.widget.DividerItemDecoration(context, android.support.v7.widget.DividerItemDecoration.VERTICAL);
        did.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(did);
    }

    public static void addAchievements(LinearLayout linearLayout, String url, Context context) {
        ImageView image = new ImageView(context);
        int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.vs_xxsmall);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPixel, sizeInPixel);
        layoutParams.setMargins(6, 6, 6, 6);
        image.setLayoutParams(layoutParams);
        linearLayout.addView(image);
        ImageLoader imageLoader = AGApplication.getImageLoader();
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }
}
