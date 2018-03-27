package ru.mos.polls;

/**
 * Список url всех сервисов проекта
 *
 * @since 1.8
 */
@Deprecated
public class UrlManager {
    public static String V210 = "2.1.0";
    public static String V220 = "2.2.0";
    public static String V230 = "2.3.0";
    public static String V240 = "2.4.0";
    public static String V241 = "2.4.1";
    public static String V250 = "2.5.0";
    public static String V251 = "2.5.1";

    public static String url(String controller, String method) {
        return String.format("v%s/%s/%s", V250/*BuildConfig.VERSION_NAME*/, controller, method);
    }

    /**
     * todo
     * после того как все методы будут готовы, убрать использование данного метода
     *
     * @param version
     * @param controller
     * @param method
     * @return
     */
    public static String url(String version, String controller, String method) {
        return String.format("v%s/%s/%s", version, controller, method);
    }

    public interface Controller {
        String POLL = "poll";
        String POLLTASK = "polltask";
        String AGPROFILE = "agprofile";
        String PROMOCODE = "PromoCode";
        String PGU = "pgu";
        String SOESG = "soesg";
        String POLL_BADGES = "pollbadges";
        String NOVELTY = "novelty";
        String NEWS = "news";
        String SUPPORT = "support";
        String USER = "user";
        String INFORAMTION = "information";
        String UTILS = "utils";
        String GEOTARGET = "geotarget";
    }

    public interface Methods {
        String GET_AREAS = "getAreas";
        String GET_OUR_APPS = "getOurApps";
        String GET_DISTRICTS = "getDistricts";
        String SELECT = "select";
        String GET_HISTORY = "getHistory";
        String GET_EXPERTS_LIST = "getExpertsList";
        String GET = "get";
        String GET_SOCIAL_INFO = "getSocialInfo";
        String NOTIFY_SOCIAL_POSTED = "notifySocialPosted";
        String FILL = "fill";
        String ADD_CODE = "AddCode";
        String HEARING_CHECK = "hearingCheck";
        String AUTH = "auth";
        String BINDING = "binding";
        String GET_POINTS = "getPoints";
        String HIDE = "hide";
        String HIDE_GROUP = "hideGroup";
        String PROFILE_GET_SOCIAL = "profileGetSocial";
        String APPROVE_OFFER = "approveOffer";
        String GET_ADDRESS_HOUSE_LIST = "getAddressHouseList";
        String ADD_FLAT = "addFlat";
        String UPDATE = "update";
        String PROFILE_UPDATE_PERSONAL = "profileUpdatePersonal";
        String GET_ADDRESS_STREET_LIST = "getAddressStreetList";
        String FIND_OBJECTS = "findObjects";
        String FIND_VARIANTS = "findVariants";
        String SET_SUBSCRIPTIONS = "setSubscriptions";
        String GET_SUBSCRIPTIONS = "getSubscriptions";
        String SET_USER_EMAIL = "setUserEmail";
        String GET_STATISTICS = "getStatistics";
        String SELECT_ACHIEVEMENTS = "selectAchievements";
        String GET_ACHIEVEMENT = "getAchievement";
        String GET_PROFILE = "getProfile";
        String GET_DISTRICT_AND_AREA = "getDistrictAndArea";
        String EVENTS_LIST = "eventsList";
        String GET_EVENT = "getEvent";
        String CHECKIN_EVENT = "checkinEvent";
        String EVENT_COMMENTS_LIST = "eventCommentsList";
        String UPDATE_EVENT_COMMENT = "updateEventComment";
        String FIND = "find";
        String GET_FEEDBACK_SUBJECTS = "getFeedbackSubjects";
        String SEND_FEEDBACK = "sendFeedback";
        String BLOCK = "block";
        String SMS_INVITATION_NOTICE = "smsInvitationNotice";
        String PROFILE_UPDATE_SOCIAL = "profileUpdateSocial";
        String APP_VERSION = "appVersion";
        String AREAS = "areas";
        String USER_IN_AREA = "userInArea";
    }

}
