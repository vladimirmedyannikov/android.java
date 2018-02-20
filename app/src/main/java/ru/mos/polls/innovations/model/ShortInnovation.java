package ru.mos.polls.innovations.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mos.polls.R;

/**
 * Стурктура данных, описывающая городскую новинку в списке всех городских новинок
 *
 * @since 1.9
 */
@Deprecated
public class ShortInnovation implements Serializable {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("DD MMMM");
    private static final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd.MM.yy");

    private long id;
    private String title;
    private int points;
    private ru.mos.polls.innovations.oldmodel.Status status;
    private long beginDate;
    private long endDate;
    private double fullRating;
    private long passedDate;

    public static List<ShortInnovation> fromJsonArray(JSONArray jsonArray) {
        List<ShortInnovation> result = null;
        if (jsonArray != null) {
            result = new ArrayList<ShortInnovation>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject json = jsonArray.optJSONObject(i);
                ShortInnovation shortInnovation = new ShortInnovation(json);
                result.add(shortInnovation);
            }
        }
        return result;
    }

    public ShortInnovation(JSONObject json) {
        if (json != null) {
            id = json.optLong("id");
            title = json.optString("title");
            points = json.optInt("points");
            status = ru.mos.polls.innovations.oldmodel.Status.parse(json.optString("status"));
            beginDate = json.optLong("begin_date") * 1000;
            endDate = json.optLong("end_date") * 1000;
            fullRating = json.optDouble("full_rating");
            passedDate = json.optLong("passed_date") * 1000;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ru.mos.polls.innovations.oldmodel.Status getStatus() {
        return status;
    }

    public void setStatus(ru.mos.polls.innovations.oldmodel.Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return hasStatus(ru.mos.polls.innovations.oldmodel.Status.ACTIVE);
    }

    public boolean isPassed() {
        return hasStatus(ru.mos.polls.innovations.oldmodel.Status.PASSED);
    }

    public boolean isOld() {
        return hasStatus(ru.mos.polls.innovations.oldmodel.Status.OLD);
    }

    private boolean hasStatus(ru.mos.polls.innovations.oldmodel.Status status) {
        return this.status == status;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getPassedDate() {
        return passedDate;
    }

    public void setPassedDate(long passedDate) {
        this.passedDate = passedDate;
    }

    public String getFormattedEndDate(Context context) {
        return String.format(context.getString(R.string.innovation_formatted_date_end), sdf.format(new Date(endDate)));
    }

    public String getTitleForAdapterItem(Context context) {
        String result = context.getString(R.string.set_mark);
        if (isOld() || isPassed()) {
            String rating = String.format("%.1f", fullRating);
            result = String.format(context.getString(R.string.mark), rating);
        }
        return result;
    }

    public String getReadableEndDate() {
        return sdfFullDate.format(new Date(endDate));
    }

    public String getReadablePassedDate() {
        /**
         * преобразовывание из милли секунд в секунды когда требуется
         */
        if (passedDate > 150000000000L) {
            return sdfFullDate.format(new Date(passedDate));
        } else {
            return sdfFullDate.format(new Date(passedDate * 1000L));
        }
    }

    public int getTitleColorForAdapterItem(Context context) {
        int result = R.color.greenText;
        if (isOld() || isPassed()) {
            result = R.color.ag_red_for_passed;
        }
        return context.getResources().getColor(result);
    }

    public double getFullRating() {
        return fullRating;
    }

    public void setFullRating(double fullRating) {
        this.fullRating = fullRating;
    }

}
