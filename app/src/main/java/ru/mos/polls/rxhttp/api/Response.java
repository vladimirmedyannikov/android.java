package ru.mos.polls.rxhttp.api;

/**
 * Модель, описывабщая стандартны формат ответа сервиса</br>
 * @see #getErrorCode() код ошибки</br>
 * @see #getErrorMessage()  текст для отображения пользователю</br>
 * @see #getResult() значимые данные ответа запроса {@link Result}</br>
 * Рекомендуется объект ответа {@link Result} помечать маркером {@link Idle}</br>
 * Используется совместно с {@link } и {@link ApiSubscriber}
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 17.05.17 10:25.
 */

public class Response<Result> {
    public static final int CODE_NO_ERROR = 0;

    private int errorCode;
    private String errorMessage;
    private Result result;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Result getResult() {
        return result;
    }

    public boolean hasError() {
        return errorCode != CODE_NO_ERROR;
    }

    /**
     *  Маркер объекта {@link #result} без данных
     */
    public interface Idle {

        boolean isEmpty();

    }
}
