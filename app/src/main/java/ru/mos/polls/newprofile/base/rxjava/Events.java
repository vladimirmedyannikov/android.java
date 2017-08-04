package ru.mos.polls.newprofile.base.rxjava;


import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;

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
        public static final int UPDATE_FLAT = 4;
        private int action;
        private AgUser agUser;
        private Flat flat;

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

        public ProfileEvents(int action, Flat flat) {
            this.action = action;
            this.flat = flat;
        }

        public Flat getFlat() {
            return flat;
        }
    }

    public static class WizardEvents {
        public static final int WIZARD_AVATAR = 1;
        public static final int WIZARD_EMAIL = 2;
        public static final int WIZARD_PERSONAL = 3;
        public static final int WIZARD_FAMALY = 4;
        public static final int WIZARD_KIDS = 5;
        public static final int WIZARD_REGISTRATION = 6;
        public static final int WIZARD_RESIDENCE = 7;
        public static final int WIZARD_WORK = 8;
        public static final int WIZARD_SOCIAL = 9;
        public static final int WIZARD_PGU = 10;

        private int wizardType;

        public WizardEvents(int wizardType) {
            this.wizardType = wizardType;
        }

        public int getWizardType() {
            return wizardType;
        }
    }
}
