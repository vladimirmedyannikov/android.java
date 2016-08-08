package ru.mos.polls.innovation.gui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.innovation.model.ShortInnovation;
import ru.mos.polls.innovation.model.Status;

/**
 * Адаптер для отображения списка городских новинок
 *
 * @since 1.9
 */
public class InnovationAdapter extends ArrayAdapter<ShortInnovation> {

    public InnovationAdapter(Context context, List<ShortInnovation> objects) {
        super(context, -1, objects);
    }

    @Override
    public int getViewTypeCount() {
        return Status.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        ShortInnovation innovation = getItem(position);
        return Arrays.binarySearch(Status.values(), innovation.getStatus());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShortInnovationHolder holder = null;
        ShortInnovation shortInnovation = getItem(position);
        int pointsValue = shortInnovation.getPoints();
        if (convertView == null) {
            switch (shortInnovation.getStatus()) {
                case ACTIVE:
                    convertView = View.inflate(getContext(), R.layout.item_innovation_active, null);
                    holder = shortInnovationTag(convertView);
                    break;
                case PASSED:
                    convertView = View.inflate(getContext(), R.layout.item_innovation_passed, null);
                    holder = shortInnovationTag(convertView);
                    break;
                case OLD:
                    convertView = View.inflate(getContext(), R.layout.item_innovation_old, null);
                    holder = shortInnovationTag(convertView);
                    break;
            }
        } else {
            holder = (ShortInnovationHolder) convertView.getTag();
        }
        switch (shortInnovation.getStatus()) {
            case ACTIVE:
                setPassedTitleAndRating(shortInnovation, holder.title, holder.ratingBar);
                if (pointsValue > 0) {
                    holder.ratingTitle.setText(getRatingTitleTxt(pointsValue));
                } else {
                    holder.ratingTitle.setText(getContext().getString(R.string.innovation_rate_your));
                }
                break;
            case PASSED:
                setPassedTitleAndRating(shortInnovation, holder.title, holder.ratingBar);
                if (pointsValue > 0) {
                    holder.creditedPoints.setVisibility(View.VISIBLE);
                    holder.voteDateTxt.setVisibility(View.GONE);
                    holder.creditedPoints.setText(getCreditedAndPassedDateTxt(shortInnovation, pointsValue));
                } else {
                    holder.creditedPoints.setVisibility(View.GONE);
                    holder.voteDateTxt.setVisibility(View.VISIBLE);
                    holder.voteDateTxt.setText(String.format(getContext().getString(R.string.vote_date_text), shortInnovation.getReadablePassedDate()));
                }
                break;
            case OLD:
                holder.innEndedDate.setText(getInnEndedDateTxt(shortInnovation));
                holder.title.setText(shortInnovation.getTitle());
                break;
        }
        return convertView;
    }

    @NonNull
    private ShortInnovationHolder shortInnovationTag(View convertView) {
        ShortInnovationHolder holder;
        holder = new ShortInnovationHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    private void setPassedTitleAndRating(ShortInnovation shortInnovation, TextView title, RatingBar ratingBar) {
        title.setText(shortInnovation.getTitle());
        ratingBar.setRating((float) shortInnovation.getFullRating());
        ratingBar.setIsIndicator(true);
    }

    private String getRatingTitleTxt(int pointsValue) {
        String pointTxt = PointsManager.getPointUnitString(getContext(), pointsValue);
        return String.format(getContext().getString(R.string.active_points_formatted), pointsValue, pointTxt);
    }

    private String getCreditedAndPassedDateTxt(ShortInnovation shortInnovation, int pointsValue) {
        String pointTxt = PointsManager.getPointUnitString(getContext(), pointsValue);
        String pointsAdded = getContext().getResources().getQuantityString(R.plurals.points_added, shortInnovation.getPoints());
        return String.format(getContext().getString(R.string.passed_points_formatted), pointsAdded, pointsValue, pointTxt, shortInnovation.getReadablePassedDate());
    }

    private String getInnEndedDateTxt(ShortInnovation shortInnovation) {
        return getContext().getString(R.string.innovation_ended) + " " + shortInnovation.getReadableEndDate();
    }

    static class ShortInnovationHolder {
        @BindView(R.id.title)
        TextView title;
        @Nullable
        @BindView(R.id.innEndedDate)
        TextView innEndedDate;
        @Nullable
        @BindView(R.id.creditedPoints)
        TextView creditedPoints;
        @Nullable
        @BindView(R.id.voteDateTxt)
        TextView voteDateTxt;
        @Nullable
        @BindView(R.id.ratingTitle)
        TextView ratingTitle;
        @Nullable
        @BindView(R.id.rating)
        RatingBar ratingBar;

        ShortInnovationHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
