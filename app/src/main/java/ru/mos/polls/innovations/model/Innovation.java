package ru.mos.polls.innovations.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Информация о городской новинке
 * <p>
 */
public class Innovation implements Serializable {

    private static final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd.MM.yy");
    private long id;

    private String title;

    @SerializedName("site_description")
    private String description;

    @SerializedName("status")
    private Status status;

    @SerializedName("begin_date")
    private long beginDate;

    @SerializedName("end_date")
    private long endDate;

    private int points;

    @SerializedName("full_rating")
    private double fullRating;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setPassedDate(long passedDate) {
        this.passedDate = passedDate;
    }

    @SerializedName("passed_date")
    private long passedDate;

    public long getPassedDate() {
        return passedDate;
    }

    public Status getStatus() {
        return status;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getPoints() {
        return points;
    }

    public double getFullRating() {
        return fullRating;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setFullRating(double fullRating) {
        this.fullRating = fullRating;
    }

    public boolean isActive() {
        return hasStatus(Status.ACTIVE);
    }

    public boolean isPassed() {
        return hasStatus(Status.PASSED);
    }

    public boolean isOld() {
        return hasStatus(Status.OLD);
    }

    private boolean hasStatus(Status status) {
        return this.status == status;
    }

    public String getReadableEndDate() {
        return sdfFullDate.format(new Date(endDate * 1000));
    }
}
