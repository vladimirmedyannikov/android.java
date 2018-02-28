package ru.mos.polls.rxhttp.rxapi.handle.error;


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
