package ru.mos.polls.newprofile.vm;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.elk.profile.Achievements;
import ru.mos.polls.AGApplication;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemAchievementBinding;
import ru.mos.polls.newprofile.ui.adapter.AchievementAdapter;

/**
 * Created by Trunks on 23.06.2017.
 */

public class AchievementVM extends RecyclerBaseViewModel<Achievements, ItemAchievementBinding> {
    ImageView badge;
    ProgressBar pb;
    OnAchievementClickListener listener;
    public static final int TYPE = 1;
    public AchievementVM(Achievements model) {
        super(model);
    }

    public void setListener(OnAchievementClickListener listener) {
        this.listener = listener;
    }

    public AchievementVM(Achievements achievement, ItemAchievementBinding binding) {
        super(achievement, binding);
        setView(binding);
    }

    private void setView(ItemAchievementBinding binding) {
        badge = binding.badgeContainer.badge;
        pb = binding.badgeContainer.loadingBadge;
        loadImage(getImageUrl());
    }

    @Override
    public int getViewType() {
        return AchievementAdapter.TYPE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_achievement;
    }

    @Override
    public void onBind(ItemAchievementBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        setView(viewDataBinding);
        viewDataBinding.setListener(listener);
    }

    @Override
    public int getVariableId() {
        return BR.viewModel;
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
