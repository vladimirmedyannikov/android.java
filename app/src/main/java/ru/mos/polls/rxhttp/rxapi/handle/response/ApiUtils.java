package ru.mos.polls.rxhttp.rxapi.handle.response;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import ru.mos.polls.rxhttp.rxapi.handle.error.ApiCodeException;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 14.06.17 11:28.
 */

public class ApiUtils<R> {
    public Function<GeneralResponse<R>, Observable<R>> processResponse() {
        return generalResponse -> {
            if (generalResponse.hasError()) {
                Throwable apiError = new ApiCodeException(generalResponse.getErrorCode(),
                        generalResponse.getErrorMessage());
                return Observable.error(apiError);
            }
            return Observable.just(generalResponse.getResult());
        };
    }

}
