package ru.mos.polls.rxhttp.api;

import android.content.Context;

import ru.mos.polls.BuildConfig;
import ru.mos.polls.rxhttp.api.session.Session;
import ru.mos.polls.util.GuiUtils;

/**
 * Обработчик ошибок по умолчанию</br>
 * @see #onServerError(int, String) отображение пользователю сообщения с текстом ошибки от сервера
 * @see #onSystemError(Throwable) отображение пользователю сообщения с общим текстом ошибки
 * @see #onNotAuthorizedServerError() оповещение о потери сессии {@link Session}
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 21.05.17 17:49.
 */

public class DefaultResponseErrorHandler implements ResponseErrorHandler {
    private Context context;

    public DefaultResponseErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onServerError(int code, String message) {
        GuiUtils.displayOkMessage(context, message, null);
    }

    @Override
    public void onSystemError(Throwable throwable) {
        String errorMessage = "Произошла непредвиденная ошибка. Попробуйте повторить операцию позже.";
        if (BuildConfig.DEBUG) {
            errorMessage = String.format("Попробуйте повторить операцию позже. Произошла непредвиденная ошибка: %s",
                    throwable.getMessage());
        }
        GuiUtils.displayOkMessage(context, errorMessage, null);
    }

    @Override
    public void onNotAuthorizedServerError() {
        Session.notifyAboutSessionIsEmpty(context);
    }
}
