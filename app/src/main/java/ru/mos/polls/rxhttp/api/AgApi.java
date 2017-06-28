package ru.mos.polls.rxhttp.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.mos.polls.newinnovation.response.ResultInnovationFill;
import ru.mos.polls.newinnovation.response.ResultInnovationGet;
import ru.mos.polls.newinnovation.response.ResultInnovationSelect;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:36.
 */

public interface AgApi {

    @POST("/v2.3.0/novelty/select?token=ag_test_token")
    Observable<Response<ResultInnovationSelect>> select(@Body Map<String, Object> fields);

    @POST("/v2.3.0/novelty/get?token=ag_test_token")
    Observable<Response<ResultInnovationGet>> get(@Body Map<String, Object> fields);

    @POST("/v2.3.0/novelty/fill?token=ag_test_token")
    Observable<Response<ResultInnovationFill>> fill(@Body Map<String, Object> fields);

}
