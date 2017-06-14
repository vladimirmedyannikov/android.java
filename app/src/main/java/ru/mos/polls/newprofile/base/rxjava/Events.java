package ru.mos.polls.newprofile.base.rxjava;


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
        private int action;

        public ProfileEvents(int action) {
            this.action = action;
        }

        public int getAction() {
            return action;
        }
    }
}
