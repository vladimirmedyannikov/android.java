package ru.mos.polls.rxhttp.api;

/**
 * Спецификация объекта-обработчика ошибок выполнения запроса
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 21.05.17 17:39.
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
     * Обработка ошибки потери сессии {@link ru.mnenie.mysmr.http.Session}
     */
    void onNotAuthorizedServerError();

}
