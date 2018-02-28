package ru.mos.polls.rxhttp.rxapi.handle.error;

import ru.mos.polls.rxhttp.session.Session;

/**
 * Спецификация объекта-обработчика ошибок выполнения запроса
 *
 */

public interface ResponseErrorHandler {

    ResponseErrorHandler STUB = new ResponseErrorHandler() {
        @Override
        public void onServerError(int code, String message) {
        }

        @Override
        public void onSystemError(Throwable throwable) {
        }

        @Override
        public void onNotAuthorizedServerError() {
        }
    };

    /**
     * Обработка ошибок со стороны сервера
     * @param code код ошибки
     * @param message текст ошибки для пользователя
     */
    void onServerError(int code, String message);

    /**
     * Обработка системных ошибок фреймворка, париснга и т.п.
     * @param throwable {@link Throwable}
     */
    void onSystemError(Throwable throwable);

    /**
     * Обработка ошибки потери сессии {@link Session}
     */
    void onNotAuthorizedServerError();

}
