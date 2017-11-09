package ru.mos.polls.poll.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;


public class Poll implements Serializable {
    private int id;
    private String title;
    private int points;
    //    private Status status;
    private String status;
    private String author;
    @SerializedName("questions_count")
    private int questCount;
    /**
     * с версии 1.8
     * тип голосование, сюда перенесли отметку о специальном опросе
     */
//    private Kind kind;
    private String kind;
    private String pushedMark;

    /**
     * @since 1.8
     * дата начала голосования
     */
    @SerializedName("begin_date")
    private long beginDate;
    /**
     * @since 1.8
     * end_date - дата окончания голосования. В случае kind=hearing_preview
     * эти даты отсутствуют (=null), и клиенту надо выводить "скоро"
     */
    @SerializedName("end_date")
    private long endDate;

    /**
     * @since 1.8
     * is_hearing_checked - этот параметр в случае kind=hearing/hearing_preview должен указывать
     * на то что пользователь дал согласие на учатие/посещение. Когда голосование перерождается
     * из hearing_preview в hearing, его значение сбрасывается в false. Для других kind параметр
     * может быть равен null или вовсе отсутствовать.
     */
    @SerializedName("is_hearing_checked")
    private boolean isHearingChecked;
    /**
     * @since 1.9
     * Дата прохождения голосования,
     * возвращается только для голосований со статусом passed {@link ru.mos.polls.poll.model.Poll.Status}
     */
    @SerializedName("passed_date")
    private long passedDate;

    public Poll(JSONObject pollJson) {
        if (pollJson != null) {
            id = pollJson.optInt("id");
            title = getString(pollJson, "title", "");
            points = pollJson.optInt("points");
//            status = Status.fromString(pollJson.optString("status"));
            status = pollJson.optString("status");
            questCount = pollJson.optInt("question_count");
            author = getString(pollJson, "author", "");

//            kind = Kind.parse(pollJson.optString("kind"));
            kind = pollJson.optString("kind");
            pushedMark = getString(pollJson, "pushed_mark", "");
            beginDate = pollJson.optLong("begin_date");
            beginDate *= 1000;
            endDate = pollJson.optLong("end_date");
            endDate *= 1000;
            passedDate = pollJson.optLong("passed_date");
            passedDate *= 1000;

            isHearingChecked = pollJson.optBoolean("is_hearing_checked");
        }
    }

    public int getPoints() {
        return points;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuestCount() {
        return questCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKind() {
        return kind;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPushedMark() {
        return pushedMark;
    }

    public void setPassedDate(long passedDate) {
        this.passedDate = passedDate;
    }

    public boolean isExistKind() {
        return kind != null;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return status.equalsIgnoreCase(Status.ACTIVE.status);
    }

    public boolean isPassed() {
        return status.equalsIgnoreCase(Status.PASSED.status);
    }

    public boolean isOld() {
        return status.equalsIgnoreCase(Status.OLD.status);
    }

    public boolean isInterrupted() {
        return status.equalsIgnoreCase(Status.INTERRUPTED.status);
    }

    public String convertFormatForDate(long mills, String newFormat) {
        SimpleDateFormat sdfNewFormat = new SimpleDateFormat(newFormat);
        return sdfNewFormat.format(mills);
    }

    public boolean isExistBeginDate() {
        return beginDate != 0;
    }

    public boolean isExistEndDate() {
        return endDate != 0;
    }

    public boolean isHearingChecked() {
        boolean result = false;
        if (kind.equalsIgnoreCase(Kind.HEARING.kind) || kind.equalsIgnoreCase(Kind.HEARING_PREVIEW.kind)) {
            result = isHearingChecked;
        }
        return result;
    }

    private String getString(JSONObject jsonObject, String tag, String defValue) {
        String result = jsonObject.optString(tag);
        if (TextUtils.isEmpty(result) || "null".equalsIgnoreCase(result)) {
            result = defValue;
        }
        return result;
    }

    public long getPassedDate() {
        return passedDate;
    }

    public enum Status {
        PASSED("passed"),
        ACTIVE("active"),
        INTERRUPTED("interrupted"),
        OLD("old");

        public String status;

        public static Status fromString(String status) {
            Status result = null;
            if (PASSED.toString().equalsIgnoreCase(status)) {
                result = PASSED;
            } else if (ACTIVE.toString().equalsIgnoreCase(status)) {
                result = ACTIVE;
            } else if (INTERRUPTED.toString().equalsIgnoreCase(status)) {
                result = INTERRUPTED;
            } else if (OLD.toString().equalsIgnoreCase(status)) {
                result = OLD;
            }

            return result;
        }

        Status(String status) {
            this.status = status;
        }


        @Override
        public String toString() {
            return status;
        }
    }
}
