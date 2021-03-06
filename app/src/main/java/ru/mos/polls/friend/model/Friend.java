package ru.mos.polls.friend.model;

import java.io.Serializable;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 11:25.
 */

public class Friend implements Serializable {
    private int id;
    private String phone;
    private String surname;
    private String name;
    private String avatar;
    private String status;

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }
}
