package ru.mos.polls.newprofile.service.model;

import java.util.List;

import ru.mos.elk.profile.AgUser;

/**
 * Created by wlTrunks on 14.07.2017.
 */

public class Personal {
    /**
     * surname : фамилия
     * firstname : имя
     * middlename : отчество
     * birthday : дата рождения, dd.mm.yyyy
     * sex : male|female|null
     * email : email
     * marital_status : married|single|null
     * childrens_count : 3
     * childrens_birthdays : ["dd.mm.yyyy","dd.mm.yyyy"]
     * social_status : 1
     */

    private String surname;
    private String firstname;
    private String middlename;
    private String birthday;
    private String sex;
    private String email;
    private String phone;
    private String marital_status;
    private Integer childrens_count;
    private transient boolean car_exists;
    private String social_status;
    private transient String troika_card_number;
    private List<String> childrens_birthdays;

    public Personal() {
    }

    public Personal(AgUser agUser) {
        surname = agUser.getSurname();
        firstname = agUser.getFirstName();
        middlename = agUser.getMiddleName();
        birthday = agUser.getBirthday();
        sex = agUser.getGender() == AgUser.Gender.NULL ? "" : agUser.getGender().getValue();
        email = agUser.getEmail();
        marital_status = agUser.getMaritalStatus() == AgUser.MaritalStatus.NULL ? "" : agUser.getMaritalStatus().getValue();
        childrens_count = agUser.getChildCount();
        social_status = agUser.getAgSocialStatus() == 0 ? "" : String.valueOf(agUser.getAgSocialStatus());
        childrens_birthdays = agUser.childBirthdaysAsList();
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public Personal setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public Personal setMarital_status(String marital_status) {
        this.marital_status = marital_status;
        return this;
    }

    public int getChildrens_count() {
        return childrens_count;
    }

    public void setChildrens_count(int childrens_count) {
        this.childrens_count = childrens_count;
    }

    public boolean isCar_exists() {
        return car_exists;
    }

    public void setCar_exists(boolean car_exists) {
        this.car_exists = car_exists;
    }

    public String getSocial_status() {
        return social_status;
    }

    public void setSocial_status(String social_status) {
        this.social_status = social_status;
    }

    public String getTroika_card_number() {
        return troika_card_number;
    }

    public void setTroika_card_number(String troika_card_number) {
        this.troika_card_number = troika_card_number;
    }

    public List<String> getChildrens_birthdays() {
        return childrens_birthdays;
    }

    public void setChildrens_birthdays(List<String> childrens_birthdays) {
        this.childrens_birthdays = childrens_birthdays;
    }
}
