package ru.mos.polls.base.rxjava;


import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.friend.model.Friend;
import ru.mos.polls.poll.model.Poll;

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
        public static final int PROFILE_LOADED = 5;
        public static final int QUEST_PROFILE_FLAT = 6;
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
        public static final int WIZARD_CHANGE_FLAT_FR = 12;
        public static final int WIZARD_SOCIAL_STATUS = 13;
        public static final int WIZARD_SOCIAL_STATUS_ONLY = 14;
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
        public static final int FRIEND_ACHIEVEMENT_DOWNLOAD_RESULT_ZERO = 3;
        public static final int FRIEND_ACHIEVEMENT_DOWNLOAD_RESULT_NOT_ZERO = 4;
        public static final int FRIEND_START_PROFILE = 5;

        private int id;
        private Friend friend;

        public FriendEvents(Friend friend) {
            this.friend = friend;
            this.id = FRIEND_START_PROFILE;
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

    public static class ProgressableEvents extends BaseEvents {
        public static final int BEGIN = 1;
        public static final int END = 2;


        public ProgressableEvents(int eventType) {
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

    public static class SourcesVotingEvents extends BaseEvents {
        public static final int SUBSCRIBE_SOURCES = 1;

        private int sourcesVotingId;
        private boolean enable;

        public SourcesVotingEvents(int sourcesVotingId, boolean enable) {
            this.sourcesVotingId = sourcesVotingId;
            this.enable = enable;
        }

        public int getSourcesVotingId() {
            return sourcesVotingId;
        }

        public boolean isEnable() {
            return enable;
        }
    }

    abstract static class BaseEvents {
        protected int eventType;

        public int getEventType() {
            return eventType;
        }
    }
}
