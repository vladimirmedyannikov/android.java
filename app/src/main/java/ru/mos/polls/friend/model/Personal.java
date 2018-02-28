package ru.mos.polls.friend.model;

import com.google.gson.annotations.SerializedName;


public class Personal {
    private String surname;
    @SerializedName("firstname")
    private String firstName;
    private String phone;
    @SerializedName("registration_date")
    private String registrationDate;
    private String avatar;

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getAvatar() {
        return avatar;
    }
}
