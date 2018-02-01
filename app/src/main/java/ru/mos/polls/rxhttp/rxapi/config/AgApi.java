package ru.mos.polls.rxhttp.rxapi.config;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.mos.polls.friend.service.FriendFind;
import ru.mos.polls.friend.service.FriendMy;
import ru.mos.polls.friend.service.FriendProfile;
import ru.mos.polls.innovations.service.NoveltyFill;
import ru.mos.polls.innovations.service.NoveltyGet;
import ru.mos.polls.innovations.service.NoveltySelect;
import ru.mos.polls.mainbanner.service.GetBannerStatistics;
import ru.mos.polls.mypoints.service.HistoryGet;
import ru.mos.polls.newquests.service.PolltaskGet;
import ru.mos.polls.poll.service.PollSelect;
import ru.mos.polls.profile.service.AchievementsGet;
import ru.mos.polls.profile.service.AchievementsSelect;
import ru.mos.polls.profile.service.AvatarSet;
import ru.mos.polls.profile.service.EmptyResponse;
import ru.mos.polls.profile.service.GetStatistics;
import ru.mos.polls.profile.service.ProfileSet;
import ru.mos.polls.profile.service.StreetGet;
import ru.mos.polls.profile.service.UploadMedia;
import ru.mos.polls.profile.service.VisibilitySet;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.sourcesvoting.service.SourcesGet;
import ru.mos.polls.sourcesvoting.service.SourcesSet;
import ru.mos.polls.support.service.FeedbackSend;
import ru.mos.polls.support.service.SubjectsLoad;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:36.
 */

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
            String SET_PROFILE = "setProfile";
            String GET_ADDRESS_STREET_LIST = "getAddressStreetList";
            String VISIBLE = "visible";
            String GET_HISTORY = "getHistory";
            String GET_FEEDBACK_SUBJECTS = "getFeedbackSubjects";
            String SEND_FEEDBACK = "sendFeedback";
            String STATISTICS = "statistics";
            String GET_SOURCES = "getSources";
            String SET_SOURCES = "setSources";
            String GET_BANNER_STATISTICS = "getBasicStatistics";
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

    @POST("/" + AgApi.Api.Versions.V_2_4_1 + "/" + AgApi.Api.Controllers.POLLTASK + "/" + Api.Methods.GET_BANNER_STATISTICS)
    Observable<GetBannerStatistics.Response> getBannerStatistics();
}
