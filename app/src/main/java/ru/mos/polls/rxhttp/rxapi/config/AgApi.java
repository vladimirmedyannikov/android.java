package ru.mos.polls.rxhttp.rxapi.config;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendFind;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendMy;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendProfile;
import ru.mos.polls.rxhttp.rxapi.model.novelty.service.NoveltyFill;
import ru.mos.polls.rxhttp.rxapi.model.novelty.service.NoveltyGet;
import ru.mos.polls.rxhttp.rxapi.model.novelty.service.NoveltySelect;

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
            String V_2_4_0 =  "v2.4.0";
            String CURRENT = V_2_3_0;
        }

        interface Controllers {
            String NOVELTY = "novelty";
            String FRIEND = "friend";
        }

        interface Methods {
            String GET = "get";
            String SELECT = "select";
            String FILL = "fill";
            String FIND = "find";
            String MY = "my";
            String PROFILE = "profile";
        }

    }

    @POST("/" + Api.Versions.CURRENT + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.SELECT)
    Observable<NoveltySelect.Response> noveltySelect(@Body NoveltySelect.Request body);

    @POST("/" + Api.Versions.CURRENT + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.GET)
    Observable<NoveltyGet.Response> noveltyGet(@Body NoveltyGet.Request body);

    @POST("/" + Api.Versions.CURRENT + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.FILL)
    Observable<NoveltyFill.Response> noveltyFill(@Body NoveltyFill.Request body);

    @POST("/" + Api.Versions.V_2_4_0 + "/"+ Api.Controllers.FRIEND + "/" + Api.Methods.FIND)
    Observable<FriendFind.Response> friendFind(@Body FriendFind.Request body);

    @POST("/" + Api.Versions.V_2_4_0 + "/"+ Api.Controllers.FRIEND + "/" + Api.Methods.MY)
    Observable<FriendMy.Response> friendMy(@Body FriendMy.Request body);

    @POST("/" + Api.Versions.V_2_4_0 + "/"+ Api.Controllers.FRIEND + "/" + Api.Methods.PROFILE)
    Observable<FriendProfile.Response> friendProfile(@Body FriendProfile.Request body);
}
