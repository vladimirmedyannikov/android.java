package ru.mos.polls.rxhttp.rxapi.handle.error;

import android.content.Context;
import android.text.TextUtils;

import ru.mos.polls.BuildConfig;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.util.GuiUtils;

/**
 * Обработчик ошибок по умолчанию</br>
 * @see #onServerError(int, String) отображение пользователю сообщения с текстом ошибки от сервера
 * @see #onSystemError(Throwable) отображение пользователю сообщения с общим текстом ошибки
 * @see #onNotAuthorizedServerError() оповещение о потери сессии {@link Session}
 *
 */

public class DefaultResponseErrorHandler implements ResponseErrorHandler {
    private Context context;

    public DefaultResponseErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onServerError(int code, String message) {
        if (TextUtils.isEmpty(message)) {
            message = "Произошла непредвиденная ошибка. Попробуйте повторить операцию позже.";
        }
        GuiUtils.displayOkMessage(context, message, null);
    }

    @Override
    public void onSystemError(Throwable throwable) {
        String errorMessage = "Произошла непредвиденная ошибка. Попробуйте повторить операцию позже.";
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace();
            errorMessage = String.format("Попробуйте повторить операцию позже. Произошла непредвиденная ошибка: %s",
                    throwable.getMessage());
        }
        GuiUtils.displayUnknownError(context/*, errorMessage*/);
    }

    @Override
    public void onNotAuthorizedServerError() {
        Session.notifyAboutSessionIsEmpty(context);
    }
}
