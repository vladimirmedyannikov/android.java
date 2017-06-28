package ru.mos.polls.rxhttp.rxapi.handle.error;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.06.17 11:59.
 */

public class ApiCodeException extends Exception {
    private int serverCode;
    private String serverMessage;

    public ApiCodeException(int code, String serverMessage) {
        this.serverCode = code;
        this.serverMessage = serverMessage;
    }

    @Override
    public String getMessage() {
        return String.format("API error: %s %s", serverCode, serverMessage);
    }

    public int getServerCode() {
        return serverCode;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
