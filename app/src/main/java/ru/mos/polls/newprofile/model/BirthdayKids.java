package ru.mos.polls.newprofile.model;

/**
 * Created by Trunks on 10.07.2017.
 */

public class BirthdayKids {
    long birthdayYear;
    String birtdayHints;
    String birthDayTitle;

    public BirthdayKids(long birthdayYear, String birtdayHints, String birthDayTitle) {
        this.birthdayYear = birthdayYear;
        this.birtdayHints = birtdayHints;
        this.birthDayTitle = birthDayTitle;
    }

    public long getBirthdayYear() {
        return birthdayYear;
    }

    public String getBirtdayHints() {
        return birtdayHints;
    }

    public String getBirthDayTitle() {
        return birthDayTitle;
    }

    public void setBirthdayYear(long birthdayYear) {
        this.birthdayYear = birthdayYear;
    }
}
