package ru.mos.polls.newinnovation.vm.item;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.newinnovation.model.Innovation;

/**
 * Created by Trunks on 03.10.2017.
 */

public class UIInnovationViewHelper {
    private static final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd.MM.yy");

    public static String getRatingTitleTxt(int pointsValue, Context context) {
        String pointTxt = PointsManager.getPointUnitString(context, pointsValue);
        return String.format(context.getString(R.string.active_points_formatted), pointsValue, pointTxt);
    }

    public static void setPassedTitleAndRating(Innovation shortInnovation, TextView title, RatingBar ratingBar) {
        title.setText(shortInnovation.getTitle());
        ratingBar.setRating((float) shortInnovation.getFullRating());
        ratingBar.setIsIndicator(true);
    }

    public static String getCreditedAndPassedDateTxt(Innovation shortInnovation, int pointsValue, Context context) {
        String pointTxt = PointsManager.getPointUnitString(context, pointsValue);
        String pointsAdded = context.getResources().getQuantityString(R.plurals.points_added, shortInnovation.getPoints());
        return String.format(context.getString(R.string.passed_points_formatted), pointsAdded, pointsValue, pointTxt, getReadablePassedDate(shortInnovation.getPassedDate()));
    }

    public static String getReadablePassedDate(long date) {
        return sdfFullDate.format(new Date(date));
    }

    public static String getInnEndedDateTxt(Innovation shortInnovation, Context context) {
        return context.getString(R.string.innovation_ended) + " " + getReadablePassedDate(shortInnovation.getEndDate() * 1000);
    }
}
