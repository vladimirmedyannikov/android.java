package ru.mos.polls.base.rxjava;


import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

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
        public static final int WIZARD_UPDATE_GENDER = 11;
        private int wizardType;
        private int percentFillProfile;

        public WizardEvents(int wizardType, int percentFillProfile) {
            this.wizardType = wizardType;
            this.percentFillProfile = percentFillProfile;
        }

        public WizardEvents(int wizardType) {
            this.wizardType = wizardType;
        }

        public int getPercentFillProfile() {
            return percentFillProfile;
        }

        public int getWizardType() {
            return wizardType;
        }
    }

    public static class FriendEvents {
        public static final int FRIEND_PROFILE = 1;
        private int id;
        private Friend friend;

        public FriendEvents(Friend friend) {
            this.friend = friend;
        }

        public Friend getFriend() {
            return friend;
        }

        public FriendEvents(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class APPEvents {
        public static final int UNAUTHORIZED = 1;

        private int eventType;

        public APPEvents(int eventType) {
            this.eventType = eventType;
        }

        public int getEventType() {
            return eventType;
        }
    }

    public static class PollEvents {
        public static final int OPEN_POLL = 1;

        private Poll poll;
        private int eventType;

        public PollEvents(int eventType, Poll poll) {
            this.eventType = eventType;
            this.poll = poll;
        }

        public Poll getPoll() {
            return poll;
        }

        public int getEventType() {
            return eventType;
        }
    }
}
