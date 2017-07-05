package ru.mos.polls.newprofile.base.rxjava;


import ru.mos.elk.profile.AgUser;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class Events {
    private Events() {
    }

    public static class Message {
        private String message;

        public Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ProfileEvents {
        public static final int EDIT_USER_INFO = 1;
        public static final int UPDATE_AVATAR = 2;
        public static final int UPDATE_USER_INFO = 3;
        private int action;
        private AgUser agUser;

        public ProfileEvents(int action) {
            this.action = action;
        }

        public ProfileEvents(int action, AgUser agUser) {
            this.action = action;
            this.agUser = agUser;
        }

        public int getAction() {
            return action;
        }

        public AgUser getAgUser() {
            return agUser;
        }
    }
}
