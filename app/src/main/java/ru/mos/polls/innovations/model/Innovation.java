package ru.mos.polls.innovations.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Информация о городской новинке
 * <p>
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:49.
 */
public class Innovation implements Serializable {
    private int id;

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

    public int getId() {
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

    public void setId(int id) {
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
}
