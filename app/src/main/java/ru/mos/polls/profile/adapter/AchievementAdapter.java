package ru.mos.polls.profile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley2.toolbox.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.profile.controller.BadgeViewController;
import ru.mos.polls.profile.model.Achievement;


public class AchievementAdapter extends ArrayAdapter<Achievement> {
    private ImageLoader imageLoader;

    public AchievementAdapter(Context context, List<Achievement> objects, ImageLoader imageLoader) {
        super(context, -1, objects);
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AchievementHolder holder;
        if (convertView != null) {
            holder = (AchievementHolder) convertView.getTag();
        } else {
            convertView = View.inflate(getContext(), R.layout.item_achievement, null);
            holder = new AchievementHolder(convertView);
            convertView.setTag(holder);
        }
        Achievement achievement = getItem(position);
        displayTitle(holder, achievement);
        displayDescription(holder, achievement);
        displayBadge(convertView, achievement, holder);
        return convertView;
    }

    private void displayTitle(AchievementHolder v, Achievement achievement) {
        v.title.setText(achievement.getTitle());
    }

    private void displayDescription(AchievementHolder v, Achievement achievement) {
        v.date.setText(achievement.getSubtitle(getContext()));
    }

    private void displayBadge(View v, Achievement achievement, AchievementHolder holder) {
        final ProgressBar loadingBadgeProgress = ButterKnife.findById(v, R.id.loadingBadge);
        BadgeViewController.displayBadge(holder.badge, loadingBadgeProgress, achievement, imageLoader);
    }

    static class AchievementHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subtitle)
        TextView date;
        @BindView(R.id.badge)
        ImageView badge;

        AchievementHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
