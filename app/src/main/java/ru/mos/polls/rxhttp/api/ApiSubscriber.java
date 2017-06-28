package ru.mos.polls.rxhttp.api;


import android.content.Context;

import ru.mos.polls.rxhttp.api.progreessable.DefaultProgressable;
import ru.mos.polls.rxhttp.api.progreessable.Progressable;
import ru.mos.polls.rxhttp.api.session.Session;
import rx.Subscriber;

/**
 * <p>
 * Обработчик {@link Subscriber} стандартного формата ответа сервиса {@link Response}
 * @see #onEmptyResult() в случае пустого ответа {@link Result}
 * @see #onResult(Object) в случае успешного выполнения запроса {@link Result}
 * @see #hasResult(Object) проверка наличия ответа {@link Result}
 * <p>
 * Обработка ошибок запроса осуществляется {@link #responseErrorHandler}, по умолчанию
 * используется обработчик  {@link DefaultResponseErrorHandler}
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 17.05.17 10:44.
 */

public abstract class ApiSubscriber<Result> extends Subscriber<Response<Result>> {

    private ResponseErrorHandler responseErrorHandler;
    private Progressable progressable;

    public ApiSubscriber(Context context) {
        this(new DefaultResponseErrorHandler(context),  new DefaultProgressable(context));
    }

    public ApiSubscriber(Context context, Progressable progressable) {
        this(new DefaultResponseErrorHandler(context),  progressable);
    }

    public ApiSubscriber(ResponseErrorHandler responseErrorHandler, Progressable progressable) {
        if (responseErrorHandler == null) {
            responseErrorHandler = ResponseErrorHandler.STUB;
        }
        if (progressable == null) {
            progressable = Progressable.STUB;
        }
        this.responseErrorHandler = responseErrorHandler;
        this.progressable = progressable;
        this.progressable.begin();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (isNeedDisplaySystemError()) {
            responseErrorHandler.onSystemError(e);
        }
        progressable.end();
    }

    @Override
    final public void onNext(Response<Result> response) {
        if (response != null) {
            if (!response.hasError()) {
                if (hasResult(response.getResult())) {
                    onResult(response.getResult());
                } else {
                    onEmptyResult();
                }
            } else {
                switch (response.getErrorCode()) {
                    case Session.ERROR_CODE_UNAUTHORIZED:
                        responseErrorHandler.onNotAuthorizedServerError();
                    default:
                        if (isNeedDisplayServerError()) {
                            responseErrorHandler.onServerError(response.getErrorCode(),
                                    response.getErrorMessage());
                        }
                        onServerError(response.getErrorCode(), response.getErrorMessage());
                        break;
                }
            }
        } else {
            onEmptyResult();
        }
        progressable.end();
    }

    /**
     * Использовать для разрешения/запрета информарования пользователя об ошибке сервиса
     *
     * @return  true - пользователю будет отображено сообщение с текстом {@link Response#getErrorMessage()}
     */
    protected boolean isNeedDisplayServerError() {
        return true;
    }

    /**
     * Использовать для разрешения/запрета информирования пользователя о системной ошибке
     *
     * @return true - пользователю будет отображено сообщение с текстом ошибки {@link Throwable#getMessage()}
     */
    protected boolean isNeedDisplaySystemError() {
        return true;
    }

    /**
     * Вызывается в случаем пустого значимого ответа {@link #hasResult(Object)} или в случае отсутсвия объекта {@link Response}
     */
    protected void onEmptyResult() {
    }

    /**
     * Проверка наличия ответа {@link Result},
     * если объект реализует маркер {@link Response.Idle},
     * то можно указать логику проверки на пустоту объекта {@link Response.Idle#isEmpty()}
     *
     * @param result объект ответа {@link Result}
     * @return true, если данные запроса есть
     */
    private boolean hasResult(Result result) {
        return result != null &&
                !(result instanceof Response.Idle && !((Response.Idle) result).isEmpty());
    }

    /**
     * Вызывается в случае наличия успешного ответа на запрос
     *
     * @param result значимые данные запроса {@link Result}
     */
    protected abstract void onResult(Result result);

    /**
     * Вызывается в случае наличия ошибки со стороны серверсайда
     *
     * @param code код ошибки
     * @param message текстошибки отображаемый пользователю
     */
    protected abstract void onServerError(int code, String message);

}
