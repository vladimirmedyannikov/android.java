package ru.mos.polls.rxhttp.rxapi.model.base;

/**
 * Модель, описывабщая стандартны формат ответа сервиса</br>
 *
 * @see #getErrorCode() код ошибки</br>
 * @see #getErrorMessage()  текст для отображения пользователю</br>
 * @see #getResult() значимые данные ответа запроса {@link Result}</br>
 * <p>
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 17.05.17 10:25.
 */

public class GeneralResponse<Result> {
    public static final int CODE_NO_ERROR = 0;
    public static final int UNAUTHORIZED = 401;
    public static final int SESSION_DOWN = 403;
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

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isUnauthorized() {
        return errorCode == UNAUTHORIZED || errorCode == SESSION_DOWN;
    }

}
