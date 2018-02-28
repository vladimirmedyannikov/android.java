package ru.mos.polls.event.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ru.mos.polls.common.model.Position;


public class EventFromList {

    public static final int NOT_CALCULATED_DISTANCE = -1;

    private long id;
    private String name;
    private String title;
    private Position position;
    @SerializedName("checkin")
    private boolean isCheckIn;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private int points;
    private String endDate;
    private String details;
    private int distance = NOT_CALCULATED_DISTANCE;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isCheckIn() {
        return isCheckIn;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getPoints() {
        return points;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDetails() {
        return details;
    }

    public int getDistance() {
        return distance;
    }

    public boolean hasPastDate() {
        return isCurrentTimeMore(endDate);
    }

    public boolean isEventYetGoing() {
        return isCurrentTimeMore(startDate);
    }

    private boolean isCurrentTimeMore(String formattedTime) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long end = sdf.parse(formattedTime).getTime();
            long current = System.currentTimeMillis();
            result = current >= end;
        } catch (ParseException ignored) {
        }
        return result;
    }

    public long getMillsOfStartDate() {
        return getMills(startDate);
    }

    public long getMillsOfEndDate() {
        return getMills(endDate);
    }

    private long getMills(String formattedTime) {
        long result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            result = sdf.parse(formattedTime).getTime();
        } catch (ParseException ignored) {
        }
        return result;
    }

    public void setChecked() {
        this.isCheckIn = true;
    }

    public void setDistance(Position currentPosition) {
        distance = Position.distance(currentPosition, position);
    }

}
