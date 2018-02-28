package ru.mos.polls.profile.model;

public class BirthdayKids {
    private long birthdayYear;
    private String birtdayHints;
    private String birthDayTitle;

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
