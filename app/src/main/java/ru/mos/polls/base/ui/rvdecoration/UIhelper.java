package ru.mos.polls.base.ui.rvdecoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;

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
        int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.vs_small);
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

    public static void hideWithFadeView(boolean on, ViewGroup rootView, View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(500);

            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(500);

            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(fade);
            transitionSet.addTransition(changeBounds);
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
            TransitionManager.beginDelayedTransition(rootView, transitionSet);
        }
        view.setVisibility(on ? View.GONE : View.VISIBLE);
    }
}
