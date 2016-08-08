package ru.mos.polls.model;

import android.os.Parcel;
import android.os.Parcelable;


public class UserPoints implements Parcelable {
    private final int burntPoints;
    private final int spentPoints;
    private final int allPoints;
    private final int currentPoints;
    private final String status;

    public UserPoints(int burntPoints, int spentPoints, int allPoints, int currentPoints, String status) {
        this.burntPoints = burntPoints;
        this.spentPoints = spentPoints;
        this.allPoints = allPoints;
        this.currentPoints = currentPoints;
        this.status = status;
    }

    public UserPoints(Parcel in) {
        this(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readString());
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getAllPoints() {
        return allPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }

    public int getBurntPoints() {
        return burntPoints;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(burntPoints);
        dest.writeInt(spentPoints);
        dest.writeInt(allPoints);
        dest.writeInt(currentPoints);
        dest.writeString(status);
    }

    public static Creator<UserPoints> CREATOR = new Creator<UserPoints>() {
        @Override
        public UserPoints createFromParcel(Parcel source) {
            return new UserPoints(source);
        }

        @Override
        public UserPoints[] newArray(int size) {
            return new UserPoints[size];
        }
    };
}
