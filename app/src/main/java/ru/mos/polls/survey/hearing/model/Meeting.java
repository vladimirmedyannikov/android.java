package ru.mos.polls.survey.hearing.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.model.Position;

/**
 * Стурктура данных, описывающая собрание публичногослушания
 *
 * @since 2.0
 */
public class Meeting implements Serializable {
    public static List<Meeting> from(JSONArray jsonArray) {
        List<Meeting> result = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                result.add(new Meeting(jsonArray.optJSONObject(i)));
            }
        }
        return result;
    }

    private long id;
    private Status status;
    private long startRegistration, endRegistration, date;
    private String address;
    private String workingHours;
    private Condition conditions;
    private String description;
    private Position position;

    public Meeting(JSONObject json) {
        if (json != null) {
            id = json.optLong("id");
            status = Status.parse(json.optString("registration_status"));
            startRegistration = json.optLong("start_registration") * 1000;
            endRegistration = json.optLong("end_registration") * 1000;
            date = json.optLong("date") * 1000;
            workingHours = json.optString("working_hours");
            address = json.optString("address");
            conditions = Condition.parse(json.optString("conditions"));
            description = json.optString("description");
            position = new Position(json.optJSONObject("position"));
        }
    }

    public long getId() {
        return id;
    }

    public long getStartRegistration() {
        return startRegistration;
    }

    public long getEndRegistration() {
        return endRegistration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public Condition getConditions() {
        return conditions;
    }

    public String getDescription() {
        return description;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() > date;
    }

    public boolean isPreview() {
        return status == Status.PREVIEW;
    }

    public boolean isRegistered() {
        return status == Status.REGISTERED;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isClosed() {
        return status == Status.CLOSED;
    }

    public enum Status {
        PREVIEW("preview"), ACTIVE("active"), REGISTERED("registered"), CLOSED("closed");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public static Status parse(String status) {
            Status result = CLOSED;
            if (REGISTERED.value.equalsIgnoreCase(status)) {
                result = REGISTERED;
            } else if (ACTIVE.value.equalsIgnoreCase(status)) {
                result = ACTIVE;
            } else if (PREVIEW.value.equalsIgnoreCase(status)) {
                result = PREVIEW;
            }
            return result;
        }
    }
}
