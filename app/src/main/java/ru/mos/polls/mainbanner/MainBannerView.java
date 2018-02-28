package ru.mos.polls.mainbanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;

public class MainBannerView extends LinearLayout {
    RecyclerView recyclerView;
    BannerAdapter adapter;

    public MainBannerView(Context context) {
        super(context);
        init();
    }

    public MainBannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainBannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("newApi")
    public MainBannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        removeAllViews();
        addView(getView());
        ButterKnife.bind(this);
    }

    public View getView() {
        View result = View.inflate(getContext(), R.layout.layout_main_list_banner, null);
        recyclerView = ButterKnife.findById(result, R.id.banner_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BannerAdapter();
        recyclerView.setAdapter(adapter);
        return result;
    }

    public void addItems(List<BannerItem> list) {
        adapter.add(list);
    }

    public void clearData() {
        adapter.clear();
    }

    public void hideViews() {
        animate()
                .translationY(-getHeight())
//                .alpha(0.0f)
                .setInterpolator(new AccelerateInterpolator(2))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(View.GONE);
                    }
                });
    }

    public void showViews() {
        setVisibility(View.VISIBLE);
        animate()
                .setListener(null)
//                .alpha(1.0f)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2));
    }
}
