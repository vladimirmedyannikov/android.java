package ru.mos.polls.rxhttp.rxapi.config;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.mos.polls.badge.controller.service.BadgesGet;
import ru.mos.polls.badge.controller.service.BadgesUpdate;
import ru.mos.polls.event.controller.service.CheckinEvent;
import ru.mos.polls.event.controller.service.GetEvent;
import ru.mos.polls.event.controller.service.GetEventCommentsList;
import ru.mos.polls.event.controller.service.GetEventsList;
import ru.mos.polls.event.controller.service.UpdateEventComment;
import ru.mos.polls.friend.service.FriendFind;
import ru.mos.polls.friend.service.FriendMy;
import ru.mos.polls.friend.service.FriendProfile;
import ru.mos.polls.geotarget.service.GetAreas;
import ru.mos.polls.geotarget.service.UserInArea;
import ru.mos.polls.informer.service.GetAppVersion;
import ru.mos.polls.innovations.service.NoveltyFill;
import ru.mos.polls.innovations.service.NoveltyGet;
import ru.mos.polls.innovations.service.NoveltySelect;
import ru.mos.polls.mainbanner.service.GetBannerStatistics;
import ru.mos.polls.mypoints.service.HistoryGet;
import ru.mos.polls.news.service.NewsGet;
import ru.mos.polls.ourapps.service.GetOurApps;
import ru.mos.polls.poll.service.PollSelect;
import ru.mos.polls.profile.controller.service.GetAchievement;
import ru.mos.polls.profile.controller.service.GetDistrictArea;
import ru.mos.polls.profile.controller.service.GetReference;
import ru.mos.polls.profile.model.Reference;
import ru.mos.polls.profile.model.flat.Value;
import ru.mos.polls.profile.service.AchievementsGet;
import ru.mos.polls.profile.service.AchievementsSelect;
import ru.mos.polls.profile.service.AvatarSet;
import ru.mos.polls.profile.service.EmptyResponse;
import ru.mos.polls.profile.service.GetStatistics;
import ru.mos.polls.profile.service.ProfileGet;
import ru.mos.polls.profile.service.ProfileSet;
import ru.mos.polls.profile.service.StreetGet;
import ru.mos.polls.profile.service.UploadMedia;
import ru.mos.polls.profile.service.VisibilitySet;
import ru.mos.polls.profile.ui.views.service.AddressesService;
import ru.mos.polls.push.service.RegisterPush;
import ru.mos.polls.quests.controller.service.Hide;
import ru.mos.polls.quests.controller.service.HideNews;
import ru.mos.polls.quests.controller.service.SmsInviteNotice;
import ru.mos.polls.quests.service.PolltaskGet;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.service.ChangePassword;
import ru.mos.polls.service.NewsFind;
import ru.mos.polls.service.PromoAddCode;
import ru.mos.polls.social.controller.service.GetSocialProfile;
import ru.mos.polls.social.controller.service.LoadPostingData;
import ru.mos.polls.social.controller.service.NotifyAboutPosting;
import ru.mos.polls.social.controller.service.ProfileUpdateSocial;
import ru.mos.polls.sourcesvoting.service.SourcesGet;
import ru.mos.polls.sourcesvoting.service.SourcesSet;
import ru.mos.polls.subscribes.service.SubscriptionService;
import ru.mos.polls.support.service.FeedbackSend;
import ru.mos.polls.support.service.SubjectsLoad;
import ru.mos.polls.survey.hearing.service.AuthPGU;
import ru.mos.polls.survey.hearing.service.HearingCheck;
import ru.mos.polls.survey.service.FillPoll;
import ru.mos.polls.survey.service.GetExpertList;
import ru.mos.polls.survey.service.GetPoll;
import ru.mos.polls.survey.variants.select.service.SelectService;


public interface AgApi {
    /**
     * Спецификация версий {@link Api.Versions},
     * наименования контроллеров {@link Api.Controllers}
     * и методов {@link Api.Methods} сервисов
     */
    interface Api {

        interface Versions {
            String V_2_3_0 = "v2.3.0";
            String V_2_4_0 = "v2.4.0";
            String V_2_4_1 = "v2.4.1";
            String V_2_5_0 = "v2.5.0";
            String CURRENT = V_2_5_0;
        }

        interface Controllers {
            String NOVELTY = "novelty";
            String FRIEND = "friends";
            String AGPROFILE = "agprofile";
            String MEDIA = "media";
            String POLL = "poll";
            String SUPPORT = "support";
            String POLLTASK = "polltask";
            String PGU = "pgu";
            String UTILS = "utils";
            String POLL_BADGES = "pollbadges";
            String GEOTARGET = "geotarget";
            String NEWS = "news";
            String PROMOCODE = "PromoCode";
            String INFORAMTION = "information";
        }

        interface Methods {
            String GET = "get";
            String SELECT = "select";
            String FILL = "fill";
            String FIND = "find";
            String MY = "my";
            String PROFILE = "profile";
            String AVATAR = "avatar";
            String UPLOAD = "upload";
            String SELECT_ACHIEVEMENTS = "selectAchievements";
            String GET_ACHIEVEMENTS = "getAchievement";
            String GET_PROFILE = "getProfile";
            String SET_PROFILE = "setProfile";
            String VISIBLE = "visible";
            String GET_HISTORY = "getHistory";
            String GET_FEEDBACK_SUBJECTS = "getFeedbackSubjects";
            String SEND_FEEDBACK = "sendFeedback";
            String STATISTICS = "statistics";
            String GET_SOURCES = "getSources";
            String SET_SOURCES = "setSources";
            String GET_BANNER_STATISTICS = "getBasicStatistics";
            String GET_DISTRICT_AND_AREA = "getDistrictAndArea";
            String GET_DISTRICTS = "getDistricts";
            String GET_AREAS = "getAreas";
            String EVENTS_LIST = "eventsList";
            String GET_EVENT = "getEvent";
            String CHECKIN_EVENT = "checkinEvent";
            String EVENT_COMMENTS_LIST = "eventCommentsList";
            String UPDATE_EVENT_COMMENT = "updateEventComment";
            String BINDING = "binding";
            String AUTH = "auth";
            String HEARING_CHECK = "hearingCheck";
            String GET_EXPERTS_LIST = "getExpertsList";
            String APP_VERSION = "appVersion";
            String GET_SOCIAL_INFO = "getSocialInfo";
            String NOTIFY_SOCIAL_POSTED = "notifySocialPosted";
            String PROFILE_GET_SOCIAL = "profileGetSocial";
            String PROFILE_UPDATE_SOCIAL = "profileUpdateSocial";
            String UPDATE = "update";
            String AREAS = "areas";
            String USER_IN_AREA = "userInArea";
            String SET_USER_EMAIL = "setUserEmail";
            String GET_SUBSCRIPTIONS = "getSubscriptions";
            String SET_SUBSCRIPTIONS = "setSubscriptions";
            String GET_ACHIEVEMENT = "getAchievement";
            String HIDE_GROUP = "hideGroup";
            String HIDE = "hide";
            String ADD_CODE = "AddCode";
            String SMS_INVITATION_NOTICE = "smsInvitationNotice";
            String FIND_OBJECTS = "findObjects";
            String FIND_VARIANTS = "findVariants";
            String GET_OUR_APPS = "getOurApps";
            String GET_ADDRESS_STREET_LIST = "getAddressStreetList";
            String GET_ADDRESS_HOUSE_LIST = "getAddressHouseList";

            String CHANGE_PASSWORD = "json/v0.2/auth/user/updatepassword";
            String RECOVERY_PASSWORD = "json/v0.3/auth/user/recoverypassword";
            String LOG_OUT = "json/v0.2/auth/user/logout";
            String PUSH_REGISTER = "json/v0.2/push/android/register";
        }
    }

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.NOVELTY + "/" + Api.Methods.SELECT)
    Observable<NoveltySelect.Response> noveltySelect(@Body NoveltySelect.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.NOVELTY + "/" + Api.Methods.GET)
    Observable<NoveltyGet.Response> noveltyGet(@Body NoveltyGet.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.NOVELTY + "/" + Api.Methods.FILL)
    Observable<NoveltyFill.Response> noveltyFill(@Body NoveltyFill.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.FRIEND + "/" + Api.Methods.FIND)
    Observable<FriendFind.Response> friendFind(@Body FriendFind.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.FRIEND + "/" + Api.Methods.MY)
    Observable<FriendMy.Response> friendMy(@Body FriendMy.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.FRIEND + "/" + Api.Methods.PROFILE)
    Observable<FriendProfile.Response> friendProfile(@Body FriendProfile.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.MEDIA + "/" + AgApi.Api.Methods.UPLOAD)
    Observable<UploadMedia.Response> uploadFile(@Body UploadMedia.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + AgApi.Api.Methods.SELECT_ACHIEVEMENTS)
    Observable<AchievementsSelect.Response> selectAchievements(@Body AchievementsSelect.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + AgApi.Api.Methods.GET_ACHIEVEMENTS)
    Observable<AchievementsGet.Response> getAchievement(@Body AchievementsGet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.SET_PROFILE)
    Observable<ProfileSet.Response> setProfile(@Body ProfileSet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_PROFILE)
    Observable<GeneralResponse<JsonObject>> getProfile(@Body AuthRequest body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_PROFILE)
    Observable<GeneralResponse<JsonObject>> login(@Body ProfileGet.LoginRequest body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_ADDRESS_STREET_LIST)
    Observable<StreetGet.Response> getAddressStreetList(@Body StreetGet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.AVATAR)
    Observable<AvatarSet.Response> setAvatar(@Body AvatarSet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.VISIBLE)
    Observable<EmptyResponse> setProfileVisibility(@Body VisibilitySet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.POLL + "/" + AgApi.Api.Methods.GET_HISTORY)
    Observable<HistoryGet.Response> getHistory(@Body HistoryGet.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.POLL + "/" + AgApi.Api.Methods.SELECT)
    Observable<PollSelect.Response> pollselect(@Body PollSelect.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.SUPPORT + "/" + AgApi.Api.Methods.GET_FEEDBACK_SUBJECTS)
    Observable<SubjectsLoad.Response> getFeedbackSubjects(@Body SubjectsLoad.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.SUPPORT + "/" + Api.Methods.SEND_FEEDBACK)
    Observable<FeedbackSend.Response> sendFeedback(@Body FeedbackSend.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.STATISTICS)
    Observable<GetStatistics.Response> getStatistics(@Body AuthRequest body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.POLL + "/" + Api.Methods.GET_SOURCES)
    Observable<SourcesGet.Response> getSources(@Body AuthRequest body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.POLL + "/" + Api.Methods.SET_SOURCES)
    Observable<EmptyResponse> setSources(@Body SourcesSet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.GET)
    Observable<PolltaskGet.Response> getPolltasks(@Body PolltaskGet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.POLLTASK + "/" + Api.Methods.GET_BANNER_STATISTICS)
    Observable<GetBannerStatistics.Response> getBannerStatistics();

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_DISTRICT_AND_AREA)
    Observable<GetDistrictArea.Response> getDistrictArea(@Body GetDistrictArea.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_DISTRICTS)
    Observable<GeneralResponse<List<Reference>>> getDistricts();

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + AgApi.Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_AREAS)
    Observable<GeneralResponse<List<Reference>>> getAreas(@Body GetReference.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.EVENTS_LIST)
    Observable<GetEventsList.Response> getEventsList(@Body GetEventsList.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.GET_EVENT)
    Observable<GetEvent.Response> getEvent(@Body GetEvent.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.CHECKIN_EVENT)
    Observable<CheckinEvent.Response> checkinEvent(@Body CheckinEvent.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.EVENT_COMMENTS_LIST)
    Observable<GetEventCommentsList.Response> getEventCommentList(@Body GetEventCommentsList.Request body);

    @POST("/" + Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.UPDATE_EVENT_COMMENT)
    Observable<UpdateEventComment.Response> updateEventComment(@Body UpdateEventComment.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.PGU + "/" + Api.Methods.BINDING)
    Observable<AuthPGU.Response> pguBinding(@Body AuthPGU.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.PGU + "/" + Api.Methods.AUTH)
    Observable<AuthPGU.Response> pguAuth(@Body AuthPGU.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.HEARING_CHECK)
    Observable<HearingCheck.Response> hearingCheck(@Body HearingCheck.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.GET_EXPERTS_LIST)
    Observable<GetExpertList.Response> getExpertsList(@Body GetExpertList.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.UTILS + "/" + Api.Methods.APP_VERSION)
    Observable<GetAppVersion.Response> getAppVersion(@Body GetAppVersion.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.GET_SOCIAL_INFO)
    Observable<LoadPostingData.Response> getSocialInfo(@Body LoadPostingData.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.NOTIFY_SOCIAL_POSTED)
    Observable<NotifyAboutPosting.Response> notifyAboutPosting(@Body NotifyAboutPosting.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.PROFILE_GET_SOCIAL)
    Observable<GetSocialProfile.Response> getSocials(@Body GetSocialProfile.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.PROFILE_UPDATE_SOCIAL)
    Observable<ProfileUpdateSocial.Response> updateSocial(@Body ProfileUpdateSocial.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL_BADGES + "/" + Api.Methods.GET)
    Observable<BadgesGet.Response> getBadges(@Body BadgesGet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL_BADGES + "/" + Api.Methods.UPDATE)
    Observable<BadgesUpdate.Response> updateBadges(@Body BadgesUpdate.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.GET)
    Observable<GeneralResponse<JsonObject>> getPoll(@Body GetPoll.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.FILL)
    Observable<FillPoll.Response> fillPoll(@Body FillPoll.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.GEOTARGET + "/" + Api.Methods.AREAS)
    Observable<GetAreas.Response> getAreas(@Body GetAreas.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.GEOTARGET + "/" + Api.Methods.USER_IN_AREA)
    Observable<UserInArea.Response> userInArea(@Body UserInArea.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_ACHIEVEMENT)
    Observable<GetAchievement.Response> getAchievement(@Body GetAchievement.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.HIDE_GROUP)
    Observable<HideNews.Response> questsHideNews(@Body HideNews.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.HIDE)
    Observable<Hide.Response> questsHide(@Body Hide.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.SET_USER_EMAIL)
    Observable<SubscriptionService.Response> setUserEMail(@Body SubscriptionService.RequestSetEmail body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_SUBSCRIPTIONS)
    Observable<GeneralResponse<JsonObject>> getSubscriptions(@Body SubscriptionService.LoadSubscription body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.SET_SUBSCRIPTIONS)
    Observable<EmptyResponse> setSubscriptions(@Body SubscriptionService.SaveSubscription body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.NEWS + "/" + Api.Methods.GET)
    Observable<NewsGet.Response> getNews(@Body NewsGet.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.NEWS + "/" + Api.Methods.FIND)
    Observable<NewsFind.Response> findNews(@Body NewsFind.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.PROMOCODE + "/" + Api.Methods.ADD_CODE)
    Observable<PromoAddCode.Response> addCode(@Body PromoAddCode.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLLTASK + "/" + Api.Methods.SMS_INVITATION_NOTICE)
    Observable<SmsInviteNotice.Response> noticeSmsInvited(@Body SmsInviteNotice.Request body);

    @POST("/" + Api.Methods.CHANGE_PASSWORD)
    Observable<GeneralResponse<String>> changePassword(@Body ChangePassword.Request body);

    @POST("/" + Api.Methods.RECOVERY_PASSWORD)
    Observable<GeneralResponse<Object>> recoveryPassword(@Body ProfileGet.RecoveryRequest body);

    @POST("/" + Api.Methods.LOG_OUT)
    Observable<EmptyResponse> logOut(@Body ProfileGet.LoginRequest body);

    @POST("/" + Api.Methods.PUSH_REGISTER)
    Observable<RegisterPush.Response> registerPush(@Body RegisterPush.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.FIND_VARIANTS)
    Observable<SelectService.VariantsResponse> findVariants(@Body SelectService.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.POLL + "/" + Api.Methods.FIND_OBJECTS)
    Observable<SelectService.ObjectsResponse> findObjects(@Body SelectService.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.INFORAMTION + "/" + Api.Methods.GET_OUR_APPS)
    Observable<GetOurApps.Response> getOurApps(@Body GetOurApps.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_ADDRESS_STREET_LIST)
    Observable<GeneralResponse<List<Value>>> getAddressStreetList(@Body AddressesService.Request body);

    @POST("/" + AgApi.Api.Versions.CURRENT + "/" + Api.Controllers.AGPROFILE + "/" + Api.Methods.GET_ADDRESS_HOUSE_LIST)
    Observable<AddressesService.Response> getAddressHouseList(@Body AddressesService.Request body);
}
