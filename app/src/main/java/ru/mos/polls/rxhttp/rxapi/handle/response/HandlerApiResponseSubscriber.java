package ru.mos.polls.rxhttp.rxapi.handle.response;

import android.content.Context;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import ru.mos.polls.rxhttp.rxapi.handle.error.DefaultResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.handle.error.ResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.progreessable.DefaultProgressable;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 15.06.17 7:51.
 */

public abstract class HandlerApiResponseSubscriber<R> implements Observer<GeneralResponse<R>> {

    private final ResponseErrorHandler errorHandler;
    private final Progressable progressable;

    public HandlerApiResponseSubscriber(final Context context) {
        this(new DefaultResponseErrorHandler(context), new DefaultProgressable(context));
    }

    public HandlerApiResponseSubscriber(final Context context, final Progressable progressable) {
        this(new DefaultResponseErrorHandler(context), progressable);
    }

    public HandlerApiResponseSubscriber(final ResponseErrorHandler  errorHandler, final Progressable progressable) {
        this.errorHandler = errorHandler;
        this.progressable = progressable;
        progressable.begin();
    }

    protected abstract void onResult(R result);

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull GeneralResponse<R> generalResponse) {
        if (generalResponse.hasError()) {
            errorHandler.onServerError(generalResponse.getErrorCode(), generalResponse.getErrorMessage());
        } else {
            onResult(generalResponse.getResult());
        }
    }

    @Override
    public void onComplete() {
        progressable.end();
    }

    @Override
    public void onError(Throwable throwable) {
        errorHandler.onSystemError(throwable);
        progressable.end();
    }
}