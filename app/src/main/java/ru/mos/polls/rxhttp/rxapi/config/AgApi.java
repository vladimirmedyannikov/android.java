package ru.mos.polls.rxhttp.rxapi.config;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
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
        }

        interface Controllers {
            String NOVELTY = "novelty";
        }

        interface Methods {
            String GET = "get";
            String SELECT = "select";
            String FILL = "fill";
        }

    }

    @POST("/" + Api.Versions.V_2_3_0 + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.SELECT)
    Observable<NoveltySelect.Response> noveltySelect(@Body NoveltySelect.Request body);

    @POST("/" + Api.Versions.V_2_3_0 + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.GET)
    Observable<NoveltyGet.Response> noveltyGet(@Body NoveltyGet.Request body);

    @POST("/" + Api.Versions.V_2_3_0 + "/"+ Api.Controllers.NOVELTY + "/" + Api.Methods.FILL)
    Observable<NoveltyFill.Response> noveltyFill(@Body NoveltyFill.Request body);

}
