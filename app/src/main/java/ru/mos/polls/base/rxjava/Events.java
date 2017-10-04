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

    public static class ProfileEvents extends BaseEvents {
        public static final int EDIT_USER_INFO = 1;
        public static final int UPDATE_AVATAR = 2;
        public static final int UPDATE_USER_INFO = 3;
        public static final int UPDATE_FLAT = 4;
        private AgUser agUser;
        private Flat flat;

        public ProfileEvents(int eventType) {
            this.eventType = eventType;
        }

        public ProfileEvents(int eventType, AgUser agUser) {
            this.eventType = eventType;
            this.agUser = agUser;
        }

        public AgUser getAgUser() {
            return agUser;
        }

        public ProfileEvents(int eventType, Flat flat) {
            this.eventType = eventType;
            this.flat = flat;
        }

        public Flat getFlat() {
            return flat;
        }
    }

    public static class WizardEvents extends BaseEvents {
        public static final int WIZARD_AVATAR = 1;
        public static final int WIZARD_EMAIL = 2;
        public static final int WIZARD_PERSONAL = 3;
        public static final int WIZARD_FAMILY = 4;
        public static final int WIZARD_KIDS = 5;
        public static final int WIZARD_REGISTRATION = 6;
        public static final int WIZARD_RESIDENCE = 7;
        public static final int WIZARD_WORK = 8;
        public static final int WIZARD_SOCIAL = 9;
        public static final int WIZARD_PGU = 10;
        public static final int WIZARD_UPDATE_GENDER = 11;
        private int percentFillProfile;

        public WizardEvents(int eventType, int percentFillProfile) {
            this.eventType = eventType;
            this.percentFillProfile = percentFillProfile;
        }

        public WizardEvents(int eventType) {
            this.eventType = eventType;
        }

        public int getPercentFillProfile() {
            return percentFillProfile;
        }

    }

    public static class FriendEvents extends BaseEvents {
        public static final int FRIEND_PROFILE = 1;
        public static final int FRIEND_INVISIBLE = 2;

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

    public static class APPEvents extends BaseEvents {
        public static final int UNAUTHORIZED = 1;


        public APPEvents(int eventType) {
            this.eventType = eventType;
        }

    }

    public static class PollEvents extends BaseEvents {
        public static final int OPEN_POLL = 1;
        public static final int FINISHED_POLL = 2;
        public static final int INTERRUPTED_POLL = 3;

        private Poll poll;
        private long pollId;

        public PollEvents(int eventType, long pollId) {
            this.eventType = eventType;
            this.pollId = pollId;
        }

        public long getPollId() {

            return pollId;
        }

        public PollEvents(int eventType, Poll poll) {
            this.eventType = eventType;
            this.poll = poll;
        }

        public Poll getPoll() {
            return poll;
        }

    }

    public static class InnovationsEvents extends BaseEvents {
        public static final int OPEN_INNOVATIONS = 1;
        public static final int PASSED_INNOVATIONS = 2;

        private long innovationId;
        private double rating;
        private long passedDate;

        public InnovationsEvents(long innovationId, int eventType) {
            this.innovationId = innovationId;
            this.eventType = eventType;
        }

        public InnovationsEvents(long innovationId, double rating, long passedDate, int eventType) {
            this.innovationId = innovationId;
            this.rating = rating;
            this.passedDate = passedDate;
            this.eventType = eventType;
        }

        public long getInnovationId() {
            return innovationId;
        }

        public double getRating() {
            return rating;
        }

        public long getPassedDate() {
            return passedDate;
        }
    }


    abstract static class BaseEvents {
        protected int eventType;

        public int getEventType() {
            return eventType;
        }
    }
}
