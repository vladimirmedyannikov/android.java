package ru.mos.polls.rxhttp.rxapi.handle.response;

import android.content.Context;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.rxhttp.rxapi.handle.error.DefaultResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.handle.error.ResponseErrorHandler;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.rxapi.progreessable.DefaultProgressable;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;


public abstract class HandlerApiResponseSubscriber<R> extends DisposableObserver<GeneralResponse<R>> {

    private ResponseErrorHandler errorHandler = ResponseErrorHandler.STUB;
    private Progressable progressable = Progressable.STUB;

    public HandlerApiResponseSubscriber() {
    }

    public HandlerApiResponseSubscriber(final Context context) {
        this(new DefaultResponseErrorHandler(context), new DefaultProgressable(context));
    }

    public HandlerApiResponseSubscriber(final Context context, final Progressable progressable) {
        this(new DefaultResponseErrorHandler(context), progressable);
    }

    public HandlerApiResponseSubscriber(final ResponseErrorHandler errorHandler, final Progressable progressable) {
        if (errorHandler == null) {
            this.errorHandler = ResponseErrorHandler.STUB;
        } else {
            this.errorHandler = errorHandler;
        }
        if (progressable == null) {
            this.progressable = Progressable.STUB;
        } else {
            this.progressable = progressable;
        }
        this.progressable.begin();
    }

    public HandlerApiResponseSubscriber(ResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected abstract void onResult(R result);

    @Override
    public void onNext(@NonNull GeneralResponse<R> generalResponse) {
        if (generalResponse.isUnauthorized()) {
            AGApplication.bus().send(new Events.APPEvents(Events.APPEvents.UNAUTHORIZED));
            return;
        }
        if (generalResponse.hasError()) {
            onHasError(generalResponse);
        } else {
            onResult(generalResponse.getResult());
        }
    }

    public void onHasError(@NonNull GeneralResponse<R> generalResponse) {
        errorHandler.onServerError(generalResponse.getErrorCode(), generalResponse.getErrorMessage());
        onErrorListener(generalResponse.getErrorCode(), generalResponse.getErrorMessage());
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

    public void onErrorListener(int code, String message) {
    }
}